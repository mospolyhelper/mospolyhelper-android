package com.mospolytech.features.account

import com.mospolytech.features.account.applications.ApplicationsViewModel
import com.mospolytech.features.account.main.AccountMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountFeaturesModule = module {
    viewModel { AccountMainViewModel() }
    viewModel { ApplicationsViewModel(get()) }
}