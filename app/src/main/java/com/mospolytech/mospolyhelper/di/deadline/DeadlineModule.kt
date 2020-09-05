package com.mospolytech.mospolyhelper.di.deadline

import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.features.ui.deadlines.DeadlineViewModel
import com.mospolytech.mospolyhelper.features.ui.deadlines.bottomdialog.DialogFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val deadlineModule = module {
    single<DeadlinesRepository> { DeadlinesRepository(get()) }

    viewModel<DeadlineViewModel> { DeadlineViewModel(get(), get()) }
    viewModel<DialogFragmentViewModel> { DialogFragmentViewModel(get(), get(), get()) }
}