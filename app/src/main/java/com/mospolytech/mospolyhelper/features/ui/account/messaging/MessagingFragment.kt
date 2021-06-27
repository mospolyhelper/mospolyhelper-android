package com.mospolytech.mospolyhelper.features.ui.account.messaging

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountMessagingBinding
import com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter.MessagesAdapter
import com.mospolytech.mospolyhelper.utils.*
import io.ktor.client.features.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.UnknownHostException

class MessagingFragment : Fragment(R.layout.fragment_account_messaging) {

    companion object {
        const val DIALOG_ID = "DialogID"
        const val NAME = "Name"
    }

    private val viewModel by viewModel<MessagingViewModel>()
    private val viewBinding by viewBinding(FragmentAccountMessagingBinding::bind)

    private val adapter = MessagesAdapter()

    var dialogId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MessagesAdapter.MY_AVATAR = viewModel.getAvatar()
        MessagesAdapter.MY_NAME = viewModel.getName()

        dialogId = arguments?.getString(DIALOG_ID).orEmpty()

        MessagesAdapter.deleteMessageClickListener = {
            lifecycleScope.launch {
                viewModel.deleteMessage(dialogId, it)
            }
        }

        lifecycleScope.launch {
            viewModel.getDialog(dialogId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerMessaging.adapter = adapter
        viewBinding.recyclerMessaging.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, true)

        arguments?.getString(NAME)?.let {
            viewBinding.toolbarDialog.title = it
        } ?: let {
            viewBinding.toolbarDialog.title = requireContext().getString(R.string.Dialog)
        }

        viewBinding.swipeMessaging.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.downloadDialog(dialogId)
            }
        }

        viewBinding.sendMessage.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sendMessage(dialogId, viewBinding.editMessage.text.toString())
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.auth.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        lifecycleScope.launch {
                            viewModel.downloadDialog(dialogId)
                        }
                    }
                    is Result0.Failure -> {
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeMessaging.isRefreshing = false
                        Toast.makeText(context, result.exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                    is Result0.Loading -> {
                        viewBinding.sendMessage.hide()
                        if (!viewBinding.swipeMessaging.isRefreshing)
                            viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dialog.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.sendMessage.show()
                        viewBinding.progressLoading.gone()
                        adapter.items = result.value
                        viewBinding.recyclerMessaging.scrollToPosition(0)
                        viewBinding.editMessage.text.clear()
                        viewBinding.swipeMessaging.isRefreshing = false
                    }
                    is Result0.Failure -> {
                        viewBinding.sendMessage.show()
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeMessaging.isRefreshing = false
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
                        viewBinding.sendMessage.hide()
                        if (!viewBinding.swipeMessaging.isRefreshing) {
                            viewBinding.progressLoading.show()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.update.collect { result ->
                when (result) {
                    is Result0.Success -> {
                        viewBinding.sendMessage.show()
                        viewBinding.progressLoading.gone()
                        adapter.items = result.value
                        viewBinding.swipeMessaging.isRefreshing = false
                    }
                    is Result0.Failure -> {
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
                        viewBinding.sendMessage.show()
                        viewBinding.progressLoading.gone()
                        viewBinding.swipeMessaging.isRefreshing = false
                    }
                    is Result0.Loading -> {
                        viewBinding.sendMessage.hide()
                        if (!viewBinding.swipeMessaging.isRefreshing) {
                            viewBinding.progressLoading.show()
                        }
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        MessagesAdapter.deleteMessageClickListener = null
        super.onDestroy()
    }
}