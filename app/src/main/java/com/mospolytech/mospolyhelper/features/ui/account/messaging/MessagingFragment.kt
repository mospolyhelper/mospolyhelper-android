package com.mospolytech.mospolyhelper.features.ui.account.messaging

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountMessagingBinding
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter.ClassmatesAdapter
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter.MessagesAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.async
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

        lifecycleScope.launch {
            viewModel.getDialog(dialogId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerMessaging.adapter = adapter
        viewBinding.recyclerMessaging.layoutManager = LinearLayoutManager(requireContext())

        viewBinding.sendMessage.setOnClickListener {
            lifecycleScope.launch {
                viewModel.sendMessage(dialogId, viewBinding.editMessage.text.toString())
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.dialog.collect { result ->
                result.onSuccess {
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    adapter.items = it
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                }.onLoading {
                    viewBinding.sendMessage.hide()
                    viewBinding.progressLoading.show()
                }
            }

            viewModel.message.collect { result ->
                result.onSuccess {
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                    adapter.items = it
                    //viewBinding.recyclerMessaging.scrollToPosition(0)
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    viewBinding.sendMessage.show()
                    viewBinding.progressLoading.gone()
                }.onLoading {
                    viewBinding.sendMessage.hide()
                    viewBinding.progressLoading.show()
                }
            }
        }

    }
}