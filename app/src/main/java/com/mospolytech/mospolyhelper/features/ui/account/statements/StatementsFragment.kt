package com.mospolytech.mospolyhelper.features.ui.account.statements

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statement
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.features.ui.account.statements.adapter.StatementsAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_statements.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatementsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel by viewModel<StatementsViewModel>()
    private var currentSemester: Int = -1
    private var semesters: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_statements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_marks.layoutManager = LinearLayoutManager(requireContext())
        recycler_marks.adapter = MarksAdapter(emptyList())

        swipe_marks.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo(semesters[semesters_spinner.selectedItemPosition])
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.statements.collect { result ->
                result.onSuccess {
                    swipe_marks.isRefreshing = false
                    progress_loading.gone()
                    filldata(it)
                }.onFailure {
                    swipe_marks.isRefreshing = false
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                }.onLoading {
                    if (!swipe_marks.isRefreshing)
                        progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
            }

    }
    private fun filldata(statements: Statements) {
        if (!semesters.containsAll(statements.semesterList)) {
            ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_dropdown_item,
                statements.semesterList.map { it.replace("/", " / ").replace("|", " год | ") + " семестр" }
            ).also { adapterSpinner ->
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                semesters_spinner.adapter = adapterSpinner
            }
            semesters = statements.semesterList
        }
        if (recycler_marks.adapter is StatementsAdapter)
            (recycler_marks.adapter as StatementsAdapter).updateList(statements.sheets)
        else recycler_marks.adapter = StatementsAdapter(statements.sheets)
        semesters_spinner.onItemSelectedListener = this
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (currentSemester == -1) {
            currentSemester = p2
        } else {
            if (recycler_marks.adapter is StatementsAdapter)
                (recycler_marks.adapter as StatementsAdapter).updateList(emptyList())
            else recycler_marks.adapter = StatementsAdapter(emptyList())
            lifecycleScope.async {
                viewModel.downloadInfo(semesters[p2])
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        if (recycler_marks.adapter is StatementsAdapter)
            (recycler_marks.adapter as StatementsAdapter).updateList(emptyList())
        else recycler_marks.adapter = StatementsAdapter(emptyList())
        lifecycleScope.async {
            viewModel.downloadInfo(semesters_spinner.selectedItem.toString())
        }
    }
}
