package com.mospolytech.mospolyhelper.di.relevant

import com.mospolytech.mospolyhelper.features.ui.relevant.RelevantViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val relevantModule = module {
    viewModel<RelevantViewModel> { RelevantViewModel(get(), get()) }
}