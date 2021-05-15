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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountStudentsBinding
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.StudentsAdapter
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.PagingLoadingAdapter
import com.mospolytech.mospolyhelper.features.ui.account.students.other.FilterEntity
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class StudentsFragment : Fragment(R.layout.fragment_account_students), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job : Job = Job()

    private val viewBinding by viewBinding(FragmentAccountStudentsBinding::bind)
    private val viewModel by viewModel<StudentsViewModel>()

    private val adapter = StudentsAdapter()
    private var clipboard: ClipboardManager? = null

    private var filters: FilterEntity = FilterEntity(mutableListOf(),mutableListOf(), mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.newCoroutineContext(this@StudentsFragment.coroutineContext)

        StudentsAdapter.groupClickListener = {
            viewBinding.editSearchStudent.setText(it)
            search(adapter)
        }

        clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        button_filter.setOnClickListener {
//            val data = bundleOf("Filter" to filters)
//            findNavController().navigate(R.id.action_studentsFragment_to_bottomDialogFilter, data)
//        }

        viewBinding.swipeStudents.setOnClickListener {
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

        viewBinding.recyclerStudents.adapter = adapter.withLoadStateFooter(
            PagingLoadingAdapter { adapter.retry() }
        )

        viewBinding.swipeStudents.setOnRefreshListener {
            if (adapter.itemCount != 0)
                adapter.refresh()
            else {
                job.cancel()
                job = Job()
                lifecycleScope.launch {
                    viewModel.fetchStudents(viewBinding.editSearchStudent.text.toString(), filters)
                        .collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                }
            }
        }
        viewBinding.editSearchStudent.setOnEditorActionListener { _, i, _ ->
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
                            if (!viewBinding.swipeStudents.isRefreshing)
                                viewBinding.progressFirstLoading.show()
                        viewBinding.textEmpty.hide()
                    }
                    is LoadState.Error -> {
                        viewBinding.progressFirstLoading.hide()
                        viewBinding.swipeStudents.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            (loadStates.refresh as LoadState.Error).error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewBinding.textEmpty.hide()
                    }
                    is LoadState.NotLoading -> {
                        viewBinding.swipeStudents.isVisible = adapter.itemCount != 0
                        viewBinding.progressFirstLoading.hide()
                        viewBinding.swipeStudents.isRefreshing = false
                        if (adapter.itemCount == 0 &&
                                    (viewBinding.editSearchStudent.text.isNotEmpty() ||
                                    filters.form.isNotEmpty() || filters.courses.isNotEmpty() ||
                                    filters.type.isNotEmpty())) {
                            viewBinding.textEmpty.show()
                        }
                    }
                    else -> viewBinding.progressFirstLoading.hide()
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

        viewBinding.recyclerStudents.setOnScrollChangeListener { view, p1, p2, p3, p4 ->
            if (p4<0) {
                viewBinding.fabStudents.hide()
            } else {
                viewBinding.fabStudents.show()
            }
        }
    }

    private fun search(adapter: StudentsAdapter) {
        adapter.submitData(lifecycle, PagingData.empty())
        viewBinding.fabStudents.isVisible = false
        job.cancel()
        job = Job()
        lifecycleScope.launch {
            viewModel.fetchStudents(viewBinding.editSearchStudent.text.toString(), filters)
                .collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
        }
    }
}