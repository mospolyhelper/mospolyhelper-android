package com.mospolytech.mospolyhelper.features.ui.account.applications

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountApplicationsBinding
import com.mospolytech.mospolyhelper.features.ui.account.applications.adapter.ApplicationsAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class ApplicationsFragment: Fragment(R.layout.fragment_account_applications) {

    private val viewBinding by viewBinding(FragmentAccountApplicationsBinding::bind)
    private val viewModel by viewModel<ApplicationsViewModel>()

    private val adapter = ApplicationsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.applicationsSwipe.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }

        viewBinding.applications.adapter = adapter

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                result?.onSuccess {
                    lifecycleScope.launch {
                        viewModel.downloadInfo()
                    }
                }?.onFailure {
                    viewBinding.progressLoading.gone()
                    viewBinding.applicationsSwipe.isRefreshing = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }?.onLoading {
                    if (!viewBinding.applicationsSwipe.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.applications.collect { result ->
                result.onSuccess {
                    viewBinding.progressLoading.gone()
                    viewBinding.applicationsSwipe.isRefreshing = false
                    adapter.items = it
                }.onFailure { error ->
                    viewBinding.progressLoading.gone()
                    viewBinding.applicationsSwipe.isRefreshing = false
                    when (error) {
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
                }.onLoading {
                    if (!viewBinding.applicationsSwipe.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

    }
}