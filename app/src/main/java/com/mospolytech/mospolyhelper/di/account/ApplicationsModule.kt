package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.data.account.applications.remote.ApplicationsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.applications.repository.ApplicationsRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.domain.account.applications.usecase.ApplicationsUseCase
import com.mospolytech.mospolyhelper.features.ui.account.applications.ApplicationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationsModule = module {
    single { ApplicationsHerokuClient(get(named("accountHerokuClient"))) }
    single { ApplicationsRemoteDataSource(get()) }
    single<ApplicationsRepository> { ApplicationsRepositoryImpl(get(), get()) }
    single { ApplicationsUseCase(get()) }
    viewModel { ApplicationsViewModel(get(), get()) }
}