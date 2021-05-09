package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.account.info.local.InfoLocalDataSource
import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.info.repository.InfoRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.features.ui.account.info.InfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val infoModule = module {
    single { InfoHerokuClient(get(named("accountHerokuClient"))) }
    single { InfoRemoteDataSource(get()) }
    single { InfoLocalDataSource(get()) }
    single<InfoRepository> { InfoRepositoryImpl(get(), get(), get(), get()) }
    single { InfoUseCase(get()) }
    viewModel { InfoViewModel(get(), get()) }
}