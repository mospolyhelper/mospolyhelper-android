package com.mospolytech.features.account

import com.mospolytech.features.account.applications.ApplicationsViewModel
import com.mospolytech.features.account.authorization.AuthViewModel
import com.mospolytech.features.account.classmates.ClassmatesViewModel
import com.mospolytech.features.account.main.AccountMainViewModel
import com.mospolytech.features.account.marks.MarksViewModel
import com.mospolytech.features.account.payments.PaymentsViewModel
import com.mospolytech.features.account.personal.PersonalViewModel
import com.mospolytech.features.account.students.StudentsViewModel
import com.mospolytech.features.account.teachers.TeachersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val accountFeaturesModule = module {
    viewModel { AccountMainViewModel() }
    viewModel { ApplicationsViewModel(get()) }
    viewModel { ClassmatesViewModel(get()) }
    viewModel { PaymentsViewModel(get()) }
    viewModel { StudentsViewModel(get()) }
    viewModel { MarksViewModel(get()) }
    viewModel { PersonalViewModel(get()) }
    viewModel { TeachersViewModel(get()) }
    viewModel { AuthViewModel() }
}