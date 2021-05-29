package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountDeadlineBinding
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.features.ui.account.deadlines.DeadlinesBottomSheetFragment.Companion.DEADLINES
import com.mospolytech.mospolyhelper.features.ui.account.deadlines.adapter.DeadlinesAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeadlinesFragment: Fragment(R.layout.fragment_account_deadline) {

    private val viewBinding by viewBinding(FragmentAccountDeadlineBinding::bind)
    private val viewModel by sharedViewModel<DeadlinesViewModel>()

    private var deadlines: List<Deadline>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.deadlineSwipe.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                result?.onSuccess {
                    lifecycleScope.launch {
                        viewModel.downloadInfo()
                    }
                }?.onFailure {
                    viewBinding.loadingSpinner.gone()
                    viewBinding.deadlineSwipe.isRefreshing = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }?.onLoading {
                    if (!viewBinding.deadlineSwipe.isRefreshing)
                        viewBinding.loadingSpinner.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.deadlines.collect { result ->
                result.onSuccess {
                    viewBinding.loadingSpinner.gone()
                    fillData(it)
                    deadlines = it
                    viewBinding.deadlineSwipe.isRefreshing = false
                }.onFailure {
                    viewBinding.loadingSpinner.gone()
                    viewBinding.deadlineSwipe.isRefreshing = false
                    if (it is ClientRequestException) {
                        if (it.response.status.value == 401) {
                            lifecycleScope.launch {
                                viewModel.refresh()
                            }
                        }
                    } else
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }.onLoading {
                    if (!viewBinding.deadlineSwipe.isRefreshing)
                        viewBinding.loadingSpinner.show()
                }
            }
        }

        viewBinding.fab.setOnClickListener {
            deadlines?.let {
                val data = bundleOf(DEADLINES to it.toTypedArray())
                findNavController().navigate(R.id.action_deadlinesFragment_to_deadlinesBottomSheetFragment, data)
            } ?: let {
                Toast.makeText(requireContext(), "Дедлайны не загружены", Toast.LENGTH_SHORT)
            }
        }
    }
    private fun fillData(deadlines: List<Deadline>) {
        viewBinding.deadlinesRecycler.adapter = DeadlinesAdapter(deadlines)
    }
}