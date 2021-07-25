package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.features.ui.account.menu.MenuViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val menuModule = module {
    viewModel { MenuViewModel(get()) }
}