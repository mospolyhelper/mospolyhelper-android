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
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_marks.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarksFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel by viewModel<MarksViewModel>()
    private lateinit var marksList: MutableList<MarkInfo>
    private lateinit var semesters: MutableList<String>
    private var currentSemester: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_marks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_marks.layoutManager = LinearLayoutManager(requireContext())
        recycler_marks.adapter = MarksAdapter(emptyList())
        swipe_marks.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.marks.collect { result ->
                result.onSuccess {
                    swipe_marks.isRefreshing = false
                    progress_loading.gone()
                    setMarks(it.marks)
                    setSemesters(it.marks)
                    button_search.isEnabled = true
                    filldata()
                }.onFailure {
                    swipe_marks.isRefreshing = false
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                }.onLoading {
                    if (!swipe_marks.isRefreshing)
                        progress_loading.show()
                    button_search.isEnabled = false
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
            }

        val editor = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (recycler_marks.adapter is MarksAdapter)
                    (recycler_marks.adapter as MarksAdapter).updateList(getMarksByName(marksList, p0.toString()))
            }
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { } }

        button_search.setOnClickListener {
            marks_search.show()
            marks_select.gone()
            edit_search_marks.addTextChangedListener(editor)
            (recycler_marks.adapter as MarksAdapter).updateList(emptyList())
            (recycler_marks.adapter as MarksAdapter).updateList(getMarksByName(marksList, ""))
            swipe_marks.isEnabled = false
        }

        button_search_clear.setOnClickListener {
            swipe_marks.isEnabled = true
            marks_search.gone()
            marks_select.show()
            edit_search_marks.removeTextChangedListener(editor)
            edit_search_marks.text.clear()
            if (semesters_spinner.adapter.count == semesters_spinner.selectedItemPosition + 1) {
                (recycler_marks.adapter as MarksAdapter).
                updateList(getMarksBySemesters(marksList, semesters_spinner.adapter.count))
            } else {
                semesters_spinner.setSelection(semesters_spinner.adapter.count - 1)
            }
        }
    }
    private fun filldata() {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            semesters.map { "$it семестр" }).also { adapterSpinner ->
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            semesters_spinner.adapter = adapterSpinner
        }
        semesters_spinner.onItemSelectedListener = this
        if (currentSemester == -1)
            semesters_spinner.setSelection(semesters_spinner.adapter.count - 1)
        else semesters_spinner.setSelection(currentSemester)
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
        if (recycler_marks.adapter is MarksAdapter)
            (recycler_marks.adapter as MarksAdapter).updateList(getMarksBySemesters(marksList, p2 + 1).toList())
        currentSemester = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        if (recycler_marks.adapter is MarksAdapter)
            (recycler_marks.adapter as MarksAdapter).updateList(mutableListOf())
    }
}
