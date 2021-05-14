package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mospolytech.mospolyhelper.R
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.databinding.FragmentAccountDialogsBinding
import com.mospolytech.mospolyhelper.features.ui.account.dialogs.adapter.DialogAdapter
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingFragment.Companion.DIALOG_ID
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DialogsFragment: Fragment(R.layout.fragment_account_dialogs) {

    private val viewModel by viewModel<DialogsViewModel>()
    private val viewBinding by viewBinding(FragmentAccountDialogsBinding::bind)

    private val adapter = DialogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DialogAdapter.dialogClickListener = {
            val data = bundleOf(DIALOG_ID to it)
            findNavController().navigate(R.id.action_dialogsFragment_to_messagingFragment, data)
        }

        lifecycleScope.launch {
            viewModel.getInfo()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.recyclerDialogs.adapter = adapter
        viewBinding.recyclerDialogs.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenResumed {
            viewModel.dialogs.collect { result ->
                result.onSuccess {
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeDialogs.isRefreshing = false
                    adapter.items = it
                }.onFailure {
                    viewBinding.progressLoading.gone()
                    viewBinding.swipeDialogs.isRefreshing = false
                    Toast.makeText(requireContext(), it.localizedMessage ?: "", Toast.LENGTH_LONG).show()
                    Log.e("json", it.message ?: "")
                }.onLoading {
                    if (!viewBinding.swipeDialogs.isRefreshing)
                        viewBinding.progressLoading.show()
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