package com.mospolytech.mospolyhelper.features.ui.account.statements

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountStatementsBinding
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.GroupMarksFragment.Companion.GUID
import com.mospolytech.mospolyhelper.features.ui.account.statements.adapter.StatementsAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class StatementsFragment : Fragment(R.layout.fragment_account_statements), AdapterView.OnItemSelectedListener {

    private val viewBinding by viewBinding(FragmentAccountStatementsBinding::bind)
    private val viewModel by viewModel<StatementsViewModel>()
    
    private var currentSemester: Int = -1
    private var semesters: List<String> = emptyList()
    private var marks: List<Statement> = emptyList()

    private val adapter = StatementsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }

        StatementsAdapter.gradeContextClickListener = {
            val data = bundleOf(GUID to it)
            findNavController().navigate(R.id.action_statementsFragment_to_groupMarksFragment, data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.recyclerMarks.adapter = adapter
        setSpinner()
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.downloadInfo()
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeMarks.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.swipeMarks.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.statements.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.swipeMarks.isRefreshing = false
                        viewBinding.progressLoading.gone()
                        viewBinding.fabShare.show()
                        fillData(result.value)
                    }
                    is Result0.Failure -> {
                        viewBinding.swipeMarks.isRefreshing = false
                        viewBinding.progressLoading.gone()
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
                        if (!viewBinding.swipeMarks.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }
    }

    private fun setListeners() {
        viewBinding.recyclerMarks.setOnScrollChangeListener { _, _, _, _, p4 ->
            if (p4<0) {
                viewBinding.fabShare.hide()
            } else {
                viewBinding.fabShare.show()
            }
        }

        viewBinding.swipeMarks.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[viewBinding.semestersSpinner.selectedItemPosition])
            }
        }

        viewBinding.fabShare.setOnClickListener {
            val sheet = getSheet(marks)
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sheet)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun getSheet(marks: List<Statement>): String =
        marks.groupBy { it.loadType }
            .toList()
            .sortedBy { it.first }
            .joinToString(separator = "\n\n") { statements ->
                StringBuilder()
                    .append(statements.first)
                    .append(":\n")
                    .append(
                        statements.second.withIndex()
                            .joinToString(separator = "\n") { statement ->
                                StringBuilder()
                                    .append(statement.index + 1)
                                    .append(") ")
                                    .append(statement.value.subject.substringBeforeLast("\r"))
                                    .append(" - ")
                                    .append(statement.value.appraisalsDate)
                                    .append(" - ")
                                    .append(
                                        if (statement.value.grade.isEmpty())
                                            getString(R.string.no_mark)
                                        else
                                            statement.value.grade
                                    )
                            }
                    )
            }

    private fun fillData(statements: Statements) {
        if (!semesters.containsAll(statements.semesterList)) {
            ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item,
                statements.semesterList.map { it.replace("/", " / ").replace("|", " год | ") + " семестр" }
            ).also { adapterSpinner ->
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewBinding.semestersSpinner.adapter = adapterSpinner
            }
            semesters = statements.semesterList
        }

        adapter.items = statements.sheets
        marks = statements.sheets

    }

    private fun setSpinner() {
        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item,
            semesters.map { it.replace("/", " / ").replace("|", " год | ") + " семестр" }
        ).also { adapterSpinner ->
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewBinding.semestersSpinner.adapter = adapterSpinner
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (currentSemester != -1 && currentSemester != p2) {
            adapter.items = emptyList()
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[p2])
            }
        }
        currentSemester = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        adapter.items = emptyList()
        lifecycleScope.launch {
            viewModel.downloadInfo(viewBinding.semestersSpinner.selectedItem.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatementsAdapter.gradeContextClickListener = null
    }

    override fun onPause() {
        super.onPause()
        viewBinding.semestersSpinner.onItemSelectedListener = null
    }

    override fun onResume() {
        super.onResume()
        viewBinding.semestersSpinner.onItemSelectedListener = this
    }
}
