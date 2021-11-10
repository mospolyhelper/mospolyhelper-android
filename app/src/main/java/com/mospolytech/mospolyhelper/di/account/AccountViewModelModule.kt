package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.features.ui.account.applications.ApplicationsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.auth.AuthViewModel
import com.mospolytech.mospolyhelper.features.ui.account.classmates.ClassmatesViewModel
import com.mospolytech.mospolyhelper.features.ui.account.deadlines.DeadlinesViewModel
import com.mospolytech.mospolyhelper.features.ui.account.dialogs.DialogsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.GroupMarksViewModel
import com.mospolytech.mospolyhelper.features.ui.account.info.InfoViewModel
import com.mospolytech.mospolyhelper.features.ui.account.marks.MarksViewModel
import com.mospolytech.mospolyhelper.features.ui.account.menu.MenuViewModel
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingViewModel
import com.mospolytech.mospolyhelper.features.ui.account.payments.PaymentsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.statements.StatementsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.students.StudentsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.teachers.TeachersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountViewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { ApplicationsViewModel(get(), get()) }
    viewModel { MenuViewModel(get()) }
    viewModel { TeachersViewModel(get(), get()) }
    viewModel { StudentsViewModel(get()) }
    viewModel { StatementsViewModel(get(), get()) }
    viewModel { PaymentsViewModel(get(), get()) }
    viewModel { MessagingViewModel(get(), get()) }
    viewModel { MarksViewModel(get(), get()) }
    viewModel { InfoViewModel(get(), get()) }
    viewModel { GroupMarksViewModel(get(), get()) }
    viewModel { DialogsViewModel(get(), get()) }
    viewModel { ClassmatesViewModel(get(), get()) }
    viewModel { DeadlinesViewModel(get(), get()) }
}