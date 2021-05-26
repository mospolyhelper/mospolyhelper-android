package com.mospolytech.mospolyhelper.features.ui.account.students

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountStudentsBinding
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.PagingLoadingAdapter
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.StudentsAdapter
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

    private var filters: FilterEntity = FilterEntity(mutableListOf(),mutableListOf(), mutableListOf())

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.newCoroutineContext(this@StudentsFragment.coroutineContext)

        StudentsAdapter.groupClickListener = {
            viewBinding.editSearchStudent.setText(it)
            search(adapter)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        button_filter.setOnClickListener {
//            val data = bundleOf("Filter" to filters)
//            findNavController().navigate(R.id.action_studentsFragment_to_bottomDialogFilter, data)
//        }

        viewBinding.fabStudents.setOnClickListener {
            var i = 1
            val names = adapter.snapshot().items.map { "${i++}. ${it.name}" }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, names.joinToString("\n"))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
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
            if (job.isCancelled) return@launch
            adapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh) {
                    is LoadState.Loading -> {
                        viewBinding.textEmpty.hide()
                        if (adapter.itemCount == 0)
                            if (!viewBinding.swipeStudents.isRefreshing)
                                viewBinding.progressFirstLoading.show()
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
                        viewBinding.progressFirstLoading.hide()
                        viewBinding.swipeStudents.isRefreshing = false
                        if (adapter.itemCount == 0 &&
                                    viewBinding.editSearchStudent.text.isNotEmpty()) {
                            viewBinding.textEmpty.show()
                        }
                    }
                }
            }
        }
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FilterEntity>("Filter")?.observe(
//            viewLifecycleOwner) { result ->
//            if (filters != result) {
//                filters = result
//                search(adapter)
//            }
//        }

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