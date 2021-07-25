package com.mospolytech.mospolyhelper.features.ui.account.marks

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountMarksBinding
import com.mospolytech.mospolyhelper.domain.account.marks.model.Mark
import com.mospolytech.mospolyhelper.domain.account.marks.model.MarkInfo
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

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
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.downloadInfo()
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.swipeMarks.isRefreshing = false
                        viewBinding.progressLoading.gone()
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
            viewModel.marks.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.swipeMarks.isRefreshing = false
                        viewBinding.progressLoading.gone()
                        setMarks(result.value.marks)
                        setSemesters(result.value.marks)
                        viewBinding.buttonSearch.isEnabled = true
                        viewBinding.fabShare.show()
                        fillData()
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
                        viewBinding.buttonSearch.isEnabled = false
                    }
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

        viewBinding.recyclerMarks.setOnScrollChangeListener { _, _, _, _, p4 ->
            if (p4<0) {
                viewBinding.fabShare.hide()
            } else {
                viewBinding.fabShare.show()
            }
        }

        viewBinding.fabShare.setOnClickListener {
            val types: MutableList<String> = mutableListOf()
            var sheet = ""
            var i = 1
            marksList.filter { it.semester == (currentSemester + 1).toString() }.forEach {
                if (!types.contains(it.loadType)) {
                    types.add(it.loadType)
                    i = 1
                    sheet += "${if (types.size>1) "\n" else ""}${it.loadType}:\n"
                }
                sheet += "${i++}) ${if (it.subject.isEmpty()) it.loadType else it.subject} " +
                        "- ${if (it.mark.isEmpty())
                            requireContext().getString(R.string.no_mark) else it.mark}\n"
            }
            sheet = sheet.substringBeforeLast("\n")
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, sheet)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
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
