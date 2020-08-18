package com.mospolytech.mospolyhelper.di.main

import com.mospolytech.mospolyhelper.features.ui.main.MainViewModel
import org.koin.dsl.module

val mainModule = module {
    // Not viewModel but single type because MainViewModel is shared with MainMenuFragment
    single<MainViewModel> { MainViewModel() }
}