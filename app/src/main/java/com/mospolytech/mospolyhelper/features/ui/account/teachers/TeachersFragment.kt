package com.mospolytech.mospolyhelper.features.ui.account.teachers

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountTeachersBinding
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingFragment.Companion.DIALOG_ID
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.PagingLoadingAdapter
import com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter.TeachersAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class TeachersFragment : Fragment(R.layout.fragment_account_teachers), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job : Job = Job()

    private val viewBinding by viewBinding(FragmentAccountTeachersBinding::bind)
    private val viewModel  by viewModel<TeachersViewModel>()

    private var adapter = TeachersAdapter()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.newCoroutineContext(this@TeachersFragment.coroutineContext)
        TeachersAdapter.teacherClickListener = {
            val data = bundleOf(DIALOG_ID to it)
            findNavController().navigate(R.id.action_teachersFragment_to_messagingFragment, data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        recycler_teachers.layoutManager = GridLayoutManager(requireContext(),
//            calculateNoOfColumns(requireContext(), 180f))

        viewBinding.recyclerTeachers.adapter = adapter.withLoadStateFooter(
            PagingLoadingAdapter { adapter.retry() }
        )

        viewBinding.swipeTeachers.setOnRefreshListener {
            if (adapter.itemCount != 0)
                adapter.refresh()
            else {
                job.cancel()
                job = Job()

                lifecycleScope.launch {
                    viewModel.fetchTeachers(viewBinding.editSearchTeacher.text.toString())
                        .collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                }
            }
        }

        viewBinding.editSearchTeacher.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                adapter.submitData(lifecycle, PagingData.empty())
                job.cancel()
                job = Job()
                lifecycleScope.launch {
                    viewModel.fetchTeachers(viewBinding.editSearchTeacher.text.toString())
                        .collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                }
                true
            } else false
        }

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                result?.onSuccess {
                    lifecycleScope.launch {
                        viewModel.fetchTeachers(viewBinding.editSearchTeacher.text.toString())
                            .collectLatest { pagingData ->
                                adapter.submitData(pagingData)
                            }
                    }
                }?.onFailure {
                    viewBinding.progressFirstLoading.gone()
                    viewBinding.swipeTeachers.isRefreshing = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }?.onLoading {
                    if (!viewBinding.swipeTeachers.isRefreshing)
                        viewBinding.progressFirstLoading.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            if (job.isCancelled) return@launchWhenResumed
            adapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh) {
                    is LoadState.Loading -> {
                        viewBinding.textEmpty.hide()
                        if (adapter.itemCount == 0)
                            if (!viewBinding.swipeTeachers.isRefreshing)
                                viewBinding.progressFirstLoading.show()
                    }
                    is LoadState.Error -> {
                        viewBinding.textEmpty.hide()
                        viewBinding.progressFirstLoading.hide()
                        viewBinding.swipeTeachers.isRefreshing = false
                        val error = (loadStates.refresh as LoadState.Error).error
                        if (error is ClientRequestException) {
                            if (error.response.status.value == 401) {
                                lifecycleScope.launch {
                                    viewModel.refresh()
                                }
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                (loadStates.refresh as LoadState.Error).error.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is LoadState.NotLoading -> {
                        if (adapter.itemCount == 0 &&
                            viewBinding.editSearchTeacher.text.isNotEmpty())
                                viewBinding.textEmpty.show()
                        viewBinding.progressFirstLoading.hide()
                        viewBinding.swipeTeachers.isRefreshing = false
                    }
                }
            }
        }


    }

    override fun onDestroy() {
        TeachersAdapter.teacherClickListener = null
        super.onDestroy()
    }

    fun calculateNoOfColumns(
        context: Context,
        columnWidthDp: Float
    ): Int {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / columnWidthDp + 0.5).toInt()
    }
}