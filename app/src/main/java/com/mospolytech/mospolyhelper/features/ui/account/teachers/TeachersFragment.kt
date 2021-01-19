package com.mospolytech.mospolyhelper.features.ui.account.teachers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.features.ui.account.students.adapter.PagingLoadingAdapter
import com.mospolytech.mospolyhelper.features.ui.account.teachers.adapter.TeachersAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_teachers.*
import kotlinx.android.synthetic.main.item_address.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class TeachersFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var job : Job = Job()


    private val viewModel  by viewModel<TeachersViewModel>()


    private val diffUtil = object : DiffUtil.ItemCallback<Teacher>() {
        override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher) = oldItem == newItem
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_teachers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.newCoroutineContext(this@TeachersFragment.coroutineContext)
//        recycler_teachers.layoutManager = GridLayoutManager(requireContext(),
//            calculateNoOfColumns(requireContext(), 180f))
        recycler_teachers.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TeachersAdapter(diffUtil) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            val data = bundleOf("DialogID" to it)
            findNavController().navigate(R.id.action_teachersFragment_to_messagingFragment, data)
        }
        recycler_teachers.adapter = adapter.withLoadStateFooter(
            PagingLoadingAdapter { adapter.retry() }
        )
        edit_search_teacher.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                adapter.submitData(lifecycle, PagingData.empty())
                job.cancel()
                job = Job()
                lifecycleScope.launch {
                    viewModel.fetchTeachers(edit_search_teacher.text.toString())
                        .collectLatest { pagingData ->
                            adapter.submitData(pagingData)
                        }
                }
                true
            } else false
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when(loadStates.refresh) {
                    is LoadState.Loading -> if (adapter.itemCount == 0) progress_first_loading.show()
                    is LoadState.Error -> {
                        progress_first_loading.hide()
                        Toast.makeText(
                            requireContext(),
                            (loadStates.refresh as LoadState.Error).error.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is LoadState.NotLoading -> {
                        if (!job.isCancelled) progress_first_loading.hide()
                    }
                    else -> if (!job.isCancelled) progress_first_loading.hide()
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        if (job.isCancelled) job = Job()
    }

    override fun onStop() {
        super.onStop()
        if (job.isActive) job.cancel()
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