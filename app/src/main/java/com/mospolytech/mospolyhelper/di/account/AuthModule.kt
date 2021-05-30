package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.features.ui.account.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { AuthViewModel(get()) }
}