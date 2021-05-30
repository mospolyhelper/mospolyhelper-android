package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.deadlines.api.DeadlinesHerokuClient
import com.mospolytech.mospolyhelper.data.account.deadlines.local.DeadlinesLocalDataSource
import com.mospolytech.mospolyhelper.data.account.deadlines.remote.DeadlinesRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.deadlines.repository.DeadlinesRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.deadlines.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.account.deadlines.usecase.DeadlinesUseCase
import com.mospolytech.mospolyhelper.features.ui.account.deadlines.DeadlinesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val deadlinesModule = module {
    single { DeadlinesHerokuClient(get(named("accountHerokuClient"))) }
    single { DeadlinesRemoteDataSource(get()) }
    single { DeadlinesLocalDataSource(get()) }
    single<DeadlinesRepository> { DeadlinesRepositoryImpl(get(), get(), get()) }
    single { DeadlinesUseCase(get()) }
    viewModel { DeadlinesViewModel(get(), get()) }
}