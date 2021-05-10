package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import androidx.fragment.app.Fragment
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAccountDialogsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DialogsFragment: Fragment(R.layout.fragment_account_dialogs) {

    //private val viewBinding by viewBinding(FragmentScheduleBinding::bind)
    private val viewModel by viewModel<DialogsViewModel>()
    private val viewBinding by viewBinding(FragmentAccountDialogsBinding::bind)


}