package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.statements.api.StatementsHerokuClient
import com.mospolytech.mospolyhelper.data.account.statements.local.StatementsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.statements.remote.StatementsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.statements.repository.StatementsRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.statements.repository.StatementsRepository
import com.mospolytech.mospolyhelper.domain.account.statements.usecase.StatementsUseCase
import com.mospolytech.mospolyhelper.features.ui.account.statements.StatementsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statementsModule = module {
    single { StatementsHerokuClient(get(named("accountHerokuClient"))) }
    single { StatementsRemoteDataSource(get()) }
    single { StatementsLocalDataSource(get()) }
    single<StatementsRepository> { StatementsRepositoryImpl(get(), get(), get()) }
    single { StatementsUseCase(get()) }
    viewModel { StatementsViewModel(get(), get(), get()) }
}