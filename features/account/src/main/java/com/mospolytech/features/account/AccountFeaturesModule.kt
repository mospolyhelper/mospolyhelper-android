package com.mospolytech.features.account

import com.mospolytech.features.account.applications.ApplicationsViewModel
import com.mospolytech.features.account.classmates.ClassmatesViewModel
import com.mospolytech.features.account.main.AccountMainViewModel
import com.mospolytech.features.account.payments.PaymentsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountFeaturesModule = module {
    viewModel { AccountMainViewModel() }
    viewModel { ApplicationsViewModel(get()) }
    viewModel { ClassmatesViewModel(get()) }
    viewModel { PaymentsViewModel(get()) }
}