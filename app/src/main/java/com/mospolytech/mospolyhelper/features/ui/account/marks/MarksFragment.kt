package com.mospolytech.mospolyhelper.features.ui.account.marks

import android.os.Bundle
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

        lifecycleScope.launchWhenResumed {
            viewModel.marks.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    marks = it
                    filldata()
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                }.onLoading {
                    progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
            }
    }
    private fun filldata() {
        recycler_marks.layoutManager = LinearLayoutManager(requireContext())
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            getSemesters(marks.marks)).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            semesters_spinner.adapter = adapter
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        recycler_marks.adapter = MarksAdapter(getMarksBySemesters(marks.marks, p2 + 1).toList())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        recycler_marks.adapter = null
    }
}