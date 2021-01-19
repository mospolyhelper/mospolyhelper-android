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
    private lateinit var marks: Marks

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
        lifecycleScope.launchWhenResumed {
            viewModel.marks.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    marks = it
                    button_search.isEnabled = true
                    filldata()
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                }.onLoading {
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
                recycler_marks.adapter = MarksAdapter(getMarksByName(marks.marks, p0.toString()))
            }
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) { } }
        button_search.setOnClickListener {
            marks_search.show()
            marks_select.gone()
            edit_search_marks.addTextChangedListener(editor)
            recycler_marks.adapter = MarksAdapter(getMarksByName(marks.marks, ""))
        }
        button_search_clear.setOnClickListener {
            marks_search.gone()
            marks_select.show()
            edit_search_marks.removeTextChangedListener(editor)
            edit_search_marks.text.clear()
            if (semesters_spinner.adapter.count == semesters_spinner.selectedItemPosition + 1) {
                recycler_marks.adapter = MarksAdapter(getMarksBySemesters(marks.marks, semesters_spinner.adapter.count).toList())
            } else {
                semesters_spinner.setSelection(semesters_spinner.adapter.count - 1)
            }
        }
    }
    private fun filldata() {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            getSemesters(marks.marks)).also { adapterSpinner ->
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            semesters_spinner.adapter = adapterSpinner
        }
        semesters_spinner.onItemSelectedListener = this
        semesters_spinner.setSelection(semesters_spinner.adapter.count - 1)
    }

    private fun getSemesters(marks: Map<String, Map<String, List<Mark>>>): MutableList<String> {
        val semesters: MutableList<String> = mutableListOf()
        marks.values.forEach { it -> it.keys.toTypedArray().forEach { it -> semesters.add("$it семестр") } }
        return semesters
    }
    private fun getMarksBySemesters(marks: Map<String, Map<String, List<Mark>>>, semester: Int): List<Mark> {
        marks.values.forEach { it -> it.forEach { it -> if (it.key == semester.toString()) return it.value } }
        return emptyList()
    }

    private fun getMarksByName(marks: Map<String, Map<String, List<Mark>>>, name: String): List<Mark> {
        val markList: MutableList<MarkInfo> = mutableListOf()
        var id = 1
        var course = 1
        var semester = 1
        marks.values.forEach {
            it.values.forEach { list ->
                list.forEach {mark ->
                    if (mark.subject.contains(name, true))
                        markList.add(MarkInfo(
                                mark.subject,
                                mark.loadType,
                                mark.mark,
                                id++,
                                semester.toString(),
                                course.toString()
                            ))
                }
                semester++
            }
        course++
        }
        return  markList.toList()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        recycler_marks.adapter = MarksAdapter(getMarksBySemesters(marks.marks, p2 + 1).toList())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        recycler_marks.adapter = null
    }
}