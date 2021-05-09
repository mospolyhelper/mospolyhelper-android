package com.mospolytech.mospolyhelper.features.ui.account.students

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.StudentsAdapter
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.PagingLoadingAdapter
import com.mospolytech.mospolyhelper.features.ui.account.students.other.FilterEntity
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.account_bottom_sheet_filter.*
import kotlinx.android.synthetic.main.fragment_account_students.*
import kotlinx.android.synthetic.main.fragment_account_students.progress_first_loading
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class StudentsFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job : Job = Job()

    private val viewModel by viewModel<StudentsViewModel>()

    private var filters: FilterEntity = FilterEntity(mutableListOf(),mutableListOf(), mutableListOf())

    private val diffUtil = object : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Student, newItem: Student) = oldItem == newItem
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.newCoroutineContext(this@StudentsFragment.coroutineContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_students.layoutManager = LinearLayoutManager(requireContext())
        val clipboard: ClipboardManager? =
            requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val adapter = StudentsAdapter(diffUtil)
        adapter.groupClick = {
            edit_search_student.setText(it)
            search(adapter)
        }

        button_filter.setOnClickListener {
            val data = bundleOf("Filter" to filters)
            findNavController().navigate(R.id.action_studentsFragment_to_bottomDialogFilter, data)
        }

        fab_students.setOnClickListener {
            var i = 1
            val names = adapter.snapshot().items.map { "${i++}. ${it.name}" }
            val clip = ClipData.newPlainText("list", names.joinToString("\n"))
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(
                requireContext(),
                "Нумерованный список скопирован в буфер обмена",
                Toast.LENGTH_SHORT
            ).show()
        }
        recycler_students.adapter = adapter.withLoadStateFooter(
            PagingLoadingAdapter { adapter.retry() }
        )
        swipe_students.setOnRefreshListener {
            if (adapter.itemCount != 0)
                adapter.refresh()
            else {
                job.cancel()
                job = Job()
                lifecycleScope.launch {
                    viewModel.fetchStudents(edit_search_student.text.toString(), filters)
                        .collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                }
            }
        }
        edit_search_student.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                search(adapter)
                true
            } else false
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh) {
                    is LoadState.Loading -> {
                        if (adapter.itemCount == 0)
                            if (!swipe_students.isRefreshing)
                                progress_first_loading.show()
                        text_empty.hide()
                    }
                    is LoadState.Error -> {
                        progress_first_loading.hide()
                        swipe_students.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            (loadStates.refresh as LoadState.Error).error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        text_empty.hide()
                    }
                    is LoadState.NotLoading -> {
                        fab_students.isVisible = adapter.itemCount != 0
                        progress_first_loading.hide()
                        swipe_students.isRefreshing = false
                        if (adapter.itemCount == 0 &&
                                    (edit_search_student.text.isNotEmpty() ||
                                    filters.form.isNotEmpty() || filters.courses.isNotEmpty() ||
                                    filters.type.isNotEmpty())) {
                            text_empty.show()
                        }
                    }
                    else -> progress_first_loading.hide()
                }
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FilterEntity>("Filter")?.observe(
            viewLifecycleOwner) { result ->
            if (filters != result) {
                filters = result
                search(adapter)
            }
        }

        recycler_students.setOnScrollChangeListener { view, p1, p2, p3, p4 ->
            if (p4<0) {
                fab_students.hide()
            } else {
                fab_students.show()
            }
        }
    }

    private fun search(adapter: StudentsAdapter) {
        adapter.submitData(lifecycle, PagingData.empty())
        fab_students.isVisible = false
        job.cancel()
        job = Job()
        lifecycleScope.launch {
            viewModel.fetchStudents(edit_search_student.text.toString(), filters)
                .collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
        }
    }
}