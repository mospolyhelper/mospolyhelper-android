package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.features.ui.account.applications.ApplicationsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.applications.adapter.ApplicationsAdapter
import com.mospolytech.mospolyhelper.features.ui.account.deadlines.adapter.DeadlinesAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_deadline.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeadlinesFragment: Fragment() {


    private val viewModel by viewModel<DeadlinesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_deadline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deadlines_recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        deadline_swipe.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo()
            }
        }

        lifecycleScope.launch {
            viewModel.deadlines.collect { result ->
                result.onSuccess {
                    loading_spinner.gone()
                    filldata(it)
                    deadline_swipe.isRefreshing = false
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    loading_spinner.gone()
                    deadline_swipe.isRefreshing = false
                }.onLoading {
                    if (!deadline_swipe.isRefreshing)
                        loading_spinner.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {

        }

        lifecycleScope.async {
            viewModel.getInfo()
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_deadlinesFragment_to_deadlinesBottomSheetFragment)
        }
    }
    fun filldata(deadlines: List<Deadline>) {
        deadlines_recycler.adapter = DeadlinesAdapter(deadlines)
    }
}