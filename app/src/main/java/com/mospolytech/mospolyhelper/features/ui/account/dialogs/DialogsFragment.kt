package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountDialogsBinding
import com.mospolytech.mospolyhelper.features.ui.account.dialogs.adapter.DialogAdapter
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingFragment.Companion.DIALOG_ID
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingFragment.Companion.NAME
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class DialogsFragment: Fragment(R.layout.fragment_account_dialogs) {

    private val viewModel by viewModel<DialogsViewModel>()
    private val viewBinding by viewBinding(FragmentAccountDialogsBinding::bind)

    private val adapter = DialogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DialogAdapter.dialogClickListener = { id, name ->
            val data = bundleOf(DIALOG_ID to id, NAME to name)
            findNavController().navigate(R.id.action_dialogsFragment_to_messagingFragment, data)
        }

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerDialogs.adapter = adapter
        viewBinding.recyclerDialogs.itemAnimator = null

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.downloadInfo()
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeDialogs.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        if (!viewBinding.swipeDialogs.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dialogs.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeDialogs.isRefreshing = false
                        adapter.items = result.value
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeDialogs.isRefreshing = false
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
                        if (!viewBinding.swipeDialogs.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        viewBinding.swipeDialogs.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadInfo()
            }
        }
    }

    override fun onDestroy() {
        DialogAdapter.dialogClickListener = null
        super.onDestroy()
    }


}