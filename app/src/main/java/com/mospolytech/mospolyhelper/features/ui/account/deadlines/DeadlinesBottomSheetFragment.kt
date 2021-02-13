package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mospolytech.mospolyhelper.R
import kotlinx.android.synthetic.main.bottom_sheet_deadline.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeadlinesBottomSheetFragment(): BottomSheetDialogFragment() {

    private val viewModel by viewModel<DeadlinesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_deadline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btadd.setOnClickListener {

        }

    }

}