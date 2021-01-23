package com.mospolytech.mospolyhelper.features.ui.account.applications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.features.ui.account.applications.adapter.ApplicationsAdapter
import com.mospolytech.mospolyhelper.features.ui.account.info.InfoViewModel
import com.mospolytech.mospolyhelper.features.ui.account.info.adapter.OrderAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_applications.*
import kotlinx.android.synthetic.main.fragment_account_applications.progress_loading
import kotlinx.android.synthetic.main.fragment_account_info.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class ApplicationsFragment: Fragment() {


    private val viewModel by viewModel<ApplicationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applications.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        applications_swipe.setOnRefreshListener {
            lifecycleScope.async {
                viewModel.downloadInfo()
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.applications.collect { result ->
                result.onSuccess {
                    progress_loading.gone()
                    filldata(it)
                    applications_swipe.isRefreshing = false
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    progress_loading.gone()
                    applications_swipe.isRefreshing = false
                }.onLoading {
                    if (!applications_swipe.isRefreshing)
                        progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getInfo()
        }
    }
    fun filldata(applicationList: List<Application>) {
        applications.adapter = ApplicationsAdapter(applicationList)
    }
}