package com.mospolytech.mospolyhelper.features.ui.account.group_marks

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountGroupMarksBinding
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.model.statement.GradeSheetMark
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
        setListeners()
        setObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun showInfo(item: GradeSheet) {
        viewBinding.nameGrade.text = item.disciplineName
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
        if (item.students.isNotEmpty()) {
            viewBinding.nameStudent.text = item.students[0].name
            viewBinding.bookStudent.text = item.students[0].recordBook
            viewBinding.markStudent.text = item.students[0].mark
        }
        if (item.teachers.isNotEmpty()) {
            viewBinding.nameTeacher.text = item.teachers[0].name
            if (item.teachers[0].signed) {
                viewBinding.nameTeacher
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fluent_person_24_regular,
                        0, R.drawable.ic_fluent_checkbox_checked_24_regular, 0)
            } else {
                viewBinding.nameTeacher
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fluent_person_24_regular,
                        0,R.drawable.ic_fluent_checkbox_unchecked_24_regular, 0)
            }
        }

    }

    @SuppressLint("RestrictedApi")
    private fun openMenu(context: Context?) {
        val menuBuilder = MenuBuilder(context)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu_grade, menuBuilder)
        menuBuilder.forEach {
            val drawable = DrawableCompat.wrap(it.icon)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(requireContext(), R.color.text_color_primary))
            it.icon = drawable
        }
        val optionsMenu = MenuPopupHelper(requireContext(), menuBuilder, viewBinding.btnMenu)
        optionsMenu.setForceShowIcon(true)

        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return onOptionsItemSelected(item)
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })
        optionsMenu.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_download -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://e.mospolytech.ru/assets/stats_marks.php?s=${guid}"))
                ContextCompat.startActivity(requireContext(), browserIntent, null)
            }
        }

        return true
    }

    private fun showMarks(items: List<GradeSheetMark>) {
        var i = 1
        adapter.items = items.map { MarksUi(i++, it.name, it.mark) }
    }
    
    private fun setListeners() {
        viewBinding.swipeGrade.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.download(guid)
            }
        }
        viewBinding.btnMenu.setOnClickListener {
            openMenu(context)
        }
        viewBinding.infoExpander.setOnClickListener {
            if (viewBinding.infoCourse.visibility == View.VISIBLE) {
                viewBinding.infoExpand.rotation = 90f
                viewBinding.infoCourse.gone()
            } else {
                viewBinding.infoExpand.rotation = -90f
                viewBinding.infoCourse.show()
            }
        }

        viewBinding.studentsExpander.setOnClickListener {
            if (viewBinding.infoStudent.visibility == View.VISIBLE) {
                viewBinding.studentsExpand.rotation = 90f
                viewBinding.infoStudent.gone()
            } else {
                viewBinding.studentsExpand.rotation = -90f
                viewBinding.infoStudent.show()
            }
        }

        viewBinding.teachersExpander.setOnClickListener {
            if (viewBinding.nameTeacher.visibility == View.VISIBLE) {
                viewBinding.teachersExpand.rotation = 90f
                viewBinding.nameTeacher.gone()
            } else {
                viewBinding.teachersExpand.rotation = -90f
                viewBinding.nameTeacher.show()
            }
        }

        viewBinding.marksExpander.setOnClickListener {
            if (viewBinding.infoMarks.visibility == View.VISIBLE) {
                viewBinding.marksExpand.rotation = 90f
                viewBinding.infoMarks.gone()
            } else {
                viewBinding.marksExpand.rotation = -90f
                viewBinding.infoMarks.show()
            }
        }
    }

    private fun setObservers() {
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
                        //viewBinding.swipeGrade.isRefreshing = false
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
}