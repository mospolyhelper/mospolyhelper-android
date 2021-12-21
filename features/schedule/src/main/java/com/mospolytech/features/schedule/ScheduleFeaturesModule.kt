package com.mospolytech.features.schedule

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduleFeaturesModule = module {
    viewModel { ScheduleViewModel(get(), get()) }
}