package com.mospolytech.mospolyhelper.features.ui.account.group_marks

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountGroupMarksBinding
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.other.MarksUi
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.gone
import com.mospolytech.mospolyhelper.utils.show
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class GroupMarksFragment: Fragment(R.layout.fragment_account_group_marks) {

    companion object {
        const val GUID = "guid"
    }

    private val viewBinding by viewBinding(FragmentAccountGroupMarksBinding::bind)
    private val viewModel by viewModel<GroupMarksViewModel>()

    private val adapter = MarksAdapter()

    private lateinit var guid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guid = arguments?.getString(GUID)!!

        lifecycleScope.launch {
            viewModel.getGradeSheet(guid)
        }
        lifecycleScope.launch {
            viewModel.getMarks(guid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.marks.adapter = adapter

        viewBinding.swipeGrade.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.download(guid)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.download(guid)
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoadingInfo.gone()
                        viewBinding.progressLoadingMarks.gone()
                        viewBinding.swipeGrade.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.swipeGrade.isRefreshing) {
                            viewBinding.progressLoadingInfo.show()
                            viewBinding.progressLoadingMarks.show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.gradeSheet.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.swipeGrade.isRefreshing = false
                        viewBinding.progressLoadingInfo.gone()
                        showInfo(result.value)
                    }
                    is Result0.Failure -> {
                        viewBinding.swipeGrade.isRefreshing = false
                        viewBinding.progressLoadingInfo.gone()
                        when (val error = result.exception) {
                            is ClientRequestException -> {
                                when (error.response.status.value) {
                                    401 ->  {
                                        lifecycleScope.launch {
                                            viewModel.refresh()
                                        }
                                    }
                                    else -> Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
                                }
                            }
                            is UnknownHostException -> {
                                Toast.makeText(context, R.string.check_connection, Toast.LENGTH_LONG).show()
                            }
                            else -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.swipeGrade.isRefreshing)
                            viewBinding.progressLoadingInfo.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.gradeSheetMarks.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.swipeGrade.isRefreshing = false
                        viewBinding.progressLoadingMarks.gone()
                        showMarks(result.value)
                    }
                    is Result0.Failure -> {
                        viewBinding.swipeGrade.isRefreshing = false
                        viewBinding.progressLoadingMarks.gone()
                        when (val error = result.exception) {
                            is ClientRequestException -> {
                                when (error.response.status.value) {
                                    401 ->  {
                                        lifecycleScope.launch {
                                            viewModel.refresh()
                                        }
                                    }
                                    else -> Toast.makeText(context, R.string.server_error, Toast.LENGTH_LONG).show()
                                }
                            }
                            is UnknownHostException -> {
                                Toast.makeText(context, R.string.check_connection, Toast.LENGTH_LONG).show()
                            }
                            else -> Toast.makeText(context, error.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.swipeGrade.isRefreshing)
                            viewBinding.progressLoadingMarks.show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(item: GradeSheet) {
        viewBinding.infoGrade.text = item.toString()
        viewBinding.dateGrade.text = requireContext().getString(R.string.grade_date, item.examType, "${item.examDate} ${item.examTime}")
        viewBinding.directionGrade.text = "${item.directionCode} ${item.direction}"
        viewBinding.facultGrade.text = item.school
        if (!item.fixed) {
            viewBinding.fixedGrade.text = requireContext().getString(R.string.grade_fixed)
            viewBinding.fixedGrade.setTextColor(requireContext().getColor(R.color.colorLow))
        } else {
            viewBinding.fixedGrade.text = requireContext().getString(R.string.grade_fixed_modified, item.modifiedDate)
            viewBinding.fixedGrade.setTextColor(requireContext().getColor(R.color.colorHigh))
        }
        viewBinding.numberGrade.text = requireContext().getString(R.string.grade_number, item.id)
        viewBinding.timeGrade.text = requireContext().getString(R.string.grade_group_info, item.year, item.course, item.semester)
        if (item.specialization.isNotEmpty()) {
            viewBinding.groupGrade.text = "${item.group}, ${item.specialization}"
        } else {
            viewBinding.groupGrade.text = item.group
        }
        viewBinding.cafGrade.text = item.department
        viewBinding.toolbarMarks.title = item.disciplineName
    }

    private fun showMarks(items: List<GradeSheetMark>) {
        var i = 1
        adapter.items = items.map { MarksUi(i++, it.name, it.mark) }
    }
}