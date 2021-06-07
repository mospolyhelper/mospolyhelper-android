package com.mospolytech.mospolyhelper.features.ui.account.statements

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountStatementsBinding
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
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
    
    //private val adapter = StatementsAdapter()
    
    private var currentSemester: Int = -1
    private var semesters: List<String> = emptyList()
    private var marks: List<Statement> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        //viewBinding.recyclerMarks.adapter = adapter

        viewBinding.swipeMarks.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[viewBinding.semestersSpinner.selectedItemPosition])
            }
        }

        viewBinding.fabShare.setOnClickListener {
            val sheet = marks.groupBy { it.loadType }
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
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sheet.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                result?.onSuccess {
                    lifecycleScope.launch {
                        viewModel.downloadInfo()
                    }
                }?.onFailure {
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeMarks.isRefreshing = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }?.onLoading {
                    if (!viewBinding.swipeMarks.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.statements.collect { result ->
                result.onSuccess {
                    viewBinding.swipeMarks.isRefreshing = false
                    viewBinding.progressLoading.gone()
                    viewBinding.fabShare.show()
                    fillData(it)
                }.onFailure { error ->
                    viewBinding.swipeMarks.isRefreshing = false
                    viewBinding.progressLoading.gone()
                    when (error) {
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
                }.onLoading {
                    if (!viewBinding.swipeMarks.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

        viewBinding.recyclerMarks.setOnScrollChangeListener { _, _, _, _, p4 ->
            if (p4<0) {
                viewBinding.fabShare.hide()
            } else {
                viewBinding.fabShare.show()
            }
        }
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

        viewBinding.recyclerMarks.adapter = StatementsAdapter(statements.sheets)
        marks = statements.sheets
        viewBinding.semestersSpinner.onItemSelectedListener = this
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (currentSemester == -1) {
            currentSemester = p2
        } else {
            viewBinding.recyclerMarks.adapter = StatementsAdapter(emptyList())
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[p2])
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        viewBinding.recyclerMarks.adapter = StatementsAdapter(emptyList())
        lifecycleScope.launch {
            viewModel.downloadInfo(viewBinding.semestersSpinner.selectedItem.toString())
        }
    }
}
