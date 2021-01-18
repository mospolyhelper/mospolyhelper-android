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
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.features.ui.account.classmates.adapter.ClassmatesAdapter
import com.mospolytech.mospolyhelper.features.ui.account.marks.adapter.MarksAdapter
import com.mospolytech.mospolyhelper.features.ui.account.messaging.adapter.MessagesAdapter
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.android.synthetic.main.fragment_account_messaging.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class MessagingFragment : Fragment() {

    private val viewModel by viewModel<MessagingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_messaging, container, false)
    }

    var dialogId: String = ""
    lateinit var dialog: MutableList<Message>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_messaging.layoutManager = LinearLayoutManager(requireContext())
        recycler_messaging.adapter = MessagesAdapter(emptyList())
        dialogId = arguments?.getString("DialogID").orEmpty()
        viewModel.setDialogId(dialogId)
        send_message.setOnClickListener {
            lifecycleScope.async {
                viewModel.sendMessage(edit_message.text.toString())
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.dialog.collect { result ->
                result.onSuccess {
                    send_message.show()
                    progress_loading.gone()
                    dialog = it.toMutableList()
                    recycler_messaging.adapter?.let { adapter ->
                        if (adapter is MessagesAdapter) adapter.updateList(dialog)
                    }
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    send_message.show()
                    progress_loading.gone()
                }.onLoading {
                    send_message.hide()
                    progress_loading.show()
                }
            }
            viewModel.message.collect { result ->
                result.onSuccess {
                    send_message.show()
                    progress_loading.gone()
                    dialog.add(it)
                    recycler_messaging.adapter?.let { adapter ->
                        if (adapter is MessagesAdapter) adapter.updateList(dialog)
                    }
                }.onFailure {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                    send_message.show()
                    progress_loading.gone()
                }.onLoading {
                    //send_message.hide()
                    //progress_loading.show()
                }
            }
        }

        lifecycleScope.async {
            viewModel.getDialog()
            }

    }
}