package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.students.remote.StudentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.students.repository.StudentsRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.features.ui.account.students.StudentsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val studentsModule = module {
    single { StudentsHerokuClient(get(named("accountHerokuClient"))) }
    single { StudentsRemoteDataSource(get(), "") }
    single<StudentsRepository> { StudentsRepositoryImpl(get(), get()) }
    single { StudentsUseCase(get()) }
    viewModel { StudentsViewModel(get(), get()) }
}