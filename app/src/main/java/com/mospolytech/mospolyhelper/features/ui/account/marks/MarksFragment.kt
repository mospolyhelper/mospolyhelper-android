package com.mospolytech.mospolyhelper.features.ui.account.marks

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountMarksBinding
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarksFragment : Fragment(R.layout.fragment_account_marks), AdapterView.OnItemSelectedListener {

    private val viewBinding by viewBinding(FragmentAccountMarksBinding::bind)
    private val viewModel by viewModel<MarksViewModel>()

    private val adapter = MarksAdapter()

    private val editor = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            adapter.items = getMarksByName(marksList, p0.toString())
        }
        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { } }

    private lateinit var marksList: MutableList<MarkInfo>
    private lateinit var semesters: MutableList<String>
    private var currentSemester: Int = -1

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
                viewModel.downloadInfo()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.marks.collect { result ->
                result.onSuccess {
                    viewBinding.swipeMarks.isRefreshing = false
                    viewBinding.progressLoading.gone()
                    setMarks(it.marks)
                    setSemesters(it.marks)
                    viewBinding.buttonSearch.isEnabled = true
                    fillData()
                }.onFailure {
                    viewBinding.swipeMarks.isRefreshing = false
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.progressLoading.gone()
                }.onLoading {
                    if (!viewBinding.swipeMarks.isRefreshing)
                        viewBinding.progressLoading.show()
                    viewBinding.buttonSearch.isEnabled = false
                }
            }
        }

        viewBinding.buttonSearch.setOnClickListener {
            viewBinding.marksSearch.show()
            viewBinding.marksSelect.gone()
            viewBinding.editSearchMarks.addTextChangedListener(editor)
            adapter.items = emptyList()
            adapter.items = getMarksByName(marksList, "")
            viewBinding.swipeMarks.isEnabled = false
        }

        viewBinding.buttonSearchClear.setOnClickListener {
            viewBinding.swipeMarks.isEnabled = true
            viewBinding.marksSearch.gone()
            viewBinding.marksSelect.show()
            viewBinding.editSearchMarks.removeTextChangedListener(editor)
            viewBinding.editSearchMarks.text.clear()
            if (viewBinding.semestersSpinner.adapter.count == viewBinding.semestersSpinner.selectedItemPosition + 1) {
                adapter.items = getMarksBySemesters(marksList, viewBinding.semestersSpinner.adapter.count)
            } else {
                viewBinding.semestersSpinner.setSelection(viewBinding.semestersSpinner.adapter.count - 1)
            }
        }

        viewBinding.recyclerMarks.setOnScrollChangeListener { view, p1, p2, p3, p4 ->
            if (p4<0) {
                viewBinding.fabShare.hide()
            } else {
                viewBinding.fabShare.show()
            }
        }
    }

    private fun fillData() {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            semesters.map { "$it семестр" }).also { adapterSpinner ->
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewBinding.semestersSpinner.adapter = adapterSpinner
        }
        viewBinding.semestersSpinner.onItemSelectedListener = this
        if (currentSemester == -1)
            viewBinding.semestersSpinner.setSelection(viewBinding.semestersSpinner.adapter.count - 1)
        else viewBinding.semestersSpinner.setSelection(currentSemester)
    }

    private fun setSemesters(marks: Map<String, Map<String, List<Mark>>>) {
        semesters = mutableListOf()
        marks.values.forEach { it -> it.keys.toTypedArray().forEach { semesters.add(it) } }
    }

    private fun getMarksBySemesters(marks: MutableList<MarkInfo>, semester: Int): List<MarkInfo> {
        val semesterMarks = mutableListOf<MarkInfo>()
        marks.forEach { if (it.semester == semester.toString()) semesterMarks.add(it) }
        return semesterMarks
    }

    private fun setMarks(marks: Map<String, Map<String, List<Mark>>>) {
        marksList = mutableListOf()
        var id = 1
        var course = 1
        var semester = 1
        marks.values.forEach {
            it.values.forEach { list ->
                list.forEach {mark ->
                    marksList.add(
                        MarkInfo(
                            mark.subject,
                            mark.loadType,
                            mark.mark,
                            id++,
                            semester.toString(),
                            course.toString()
                        )
                    )
                }
                semester++
            }
            course++
        }
    }

    private fun getMarksByName(marks: MutableList<MarkInfo>, name: String): List<MarkInfo> {
        val marksList = mutableListOf<MarkInfo>()
        marksList.addAll(marks)
        return marksList.filter { it.subject.contains(name, true) }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        adapter.items = getMarksBySemesters(marksList, p2 + 1).toList()
        currentSemester = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        adapter.items = mutableListOf()
    }
}
