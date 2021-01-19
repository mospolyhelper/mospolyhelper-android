package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.classmates.api.ClassmatesHerokuClient
import com.mospolytech.mospolyhelper.data.account.classmates.local.ClassmatesLocalDataSource
import com.mospolytech.mospolyhelper.data.account.classmates.remote.ClassmatesRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.classmates.repository.ClassmatesRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.classmates.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.domain.account.classmates.usecase.ClassmatesUseCase
import com.mospolytech.mospolyhelper.features.ui.account.classmates.ClassmatesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val classmatesModule = module {
    single { ClassmatesHerokuClient(get(named("accountHerokuClient"))) }
    single { ClassmatesRemoteDataSource(get()) }
    single { ClassmatesLocalDataSource(get()) }
    single<ClassmatesRepository> { ClassmatesRepositoryImpl(get(), get(), get()) }
    single { ClassmatesUseCase(get()) }
    viewModel { ClassmatesViewModel(get(), get()) }
}