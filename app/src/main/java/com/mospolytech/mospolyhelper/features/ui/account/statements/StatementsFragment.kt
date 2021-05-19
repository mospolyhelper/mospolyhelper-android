package com.mospolytech.mospolyhelper.features.ui.account.statements

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
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.features.ui.account.statements.adapter.StatementsAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatementsFragment : Fragment(R.layout.fragment_account_statements), AdapterView.OnItemSelectedListener {

    private val viewBinding by viewBinding(FragmentAccountStatementsBinding::bind)
    private val viewModel by viewModel<StatementsViewModel>()
    
    private val adapter = StatementsAdapter()
    
    private var currentSemester: Int = -1
    private var semesters: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewBinding.recyclerMarks.adapter = adapter

        viewBinding.swipeMarks.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[viewBinding.semestersSpinner.selectedItemPosition])
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.statements.collect { result ->
                result.onSuccess {
                    viewBinding.swipeMarks.isRefreshing = false
                    viewBinding.progressLoading.gone()
                    fillData(it)
                }.onFailure {
                    viewBinding.swipeMarks.isRefreshing = false
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.progressLoading.gone()
                }.onLoading {
                    if (!viewBinding.swipeMarks.isRefreshing)
                        viewBinding.progressLoading.show()
                }
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

        adapter.items = statements.sheets
        viewBinding.semestersSpinner.onItemSelectedListener = this
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (currentSemester == -1) {
            currentSemester = p2
        } else {
            adapter.items = emptyList()
            lifecycleScope.launch {
                viewModel.downloadInfo(semesters[p2])
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        adapter.items = emptyList()
        lifecycleScope.launch {
            viewModel.downloadInfo(viewBinding.semestersSpinner.selectedItem.toString())
        }
    }
}
