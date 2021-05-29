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

class MessagingFragment : Fragment(R.layout.fragment_account_messaging) {

    companion object {
        const val DIALOG_ID = "DialogID"
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
                result?.onSuccess {
                    lifecycleScope.launch {
                        viewModel.downloadDialog(dialogId)
                    }
                }?.onFailure {
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeMessaging.isRefreshing = false
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }?.onLoading {
                    viewBinding.sendMessage.hide()
                    if (!viewBinding.swipeMessaging.isRefreshing)
                        viewBinding.progressLoading.show()
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dialog.collect { result ->
                result.onSuccess {
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    adapter.items = it
                    viewBinding.recyclerMessaging.scrollToPosition(0)
                    viewBinding.editMessage.text.clear()
                    viewBinding.swipeMessaging.isRefreshing = false
                }.onFailure {
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeMessaging.isRefreshing = false
                    if (it is ClientRequestException) {
                        if (it.response.status.value == 401) {
                            lifecycleScope.launch {
                                viewModel.refresh()
                            }
                        }
                    } else
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }.onLoading {
                    viewBinding.sendMessage.hide()
                    if (!viewBinding.swipeMessaging.isRefreshing) {
                        viewBinding.progressLoading.show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.update.collect { result ->
                result.onSuccess {
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    adapter.items = it
                    viewBinding.swipeMessaging.isRefreshing = false
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeMessaging.isRefreshing = false
                }.onLoading {
                    viewBinding.sendMessage.hide()
                    if (!viewBinding.swipeMessaging.isRefreshing) {
                        viewBinding.progressLoading.show()
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