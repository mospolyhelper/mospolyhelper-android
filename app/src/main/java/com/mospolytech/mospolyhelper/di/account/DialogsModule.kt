package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.dialogs.api.DialogsHerokuClient
import com.mospolytech.mospolyhelper.data.account.dialogs.local.DialogsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.dialogs.remote.DialogsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.dialogs.repository.DialogsRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.domain.account.dialogs.usecase.DialogsUseCase
import com.mospolytech.mospolyhelper.features.ui.account.dialogs.DialogsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dialogsModule = module {
    single { DialogsHerokuClient(get(named("accountHerokuClient"))) }
    single { DialogsRemoteDataSource(get()) }
    single { DialogsLocalDataSource(get()) }
    single<DialogsRepository> { DialogsRepositoryImpl(get(), get(), get()) }
    single { DialogsUseCase(get()) }
    viewModel { DialogsViewModel(get(), get()) }
}