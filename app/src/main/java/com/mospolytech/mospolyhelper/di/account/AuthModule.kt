package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.auth.api.AuthJwtHerokuClient
import com.mospolytech.mospolyhelper.data.account.auth.local.AuthLocalDataSource
import com.mospolytech.mospolyhelper.data.account.auth.remote.AuthRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.auth.repository.AuthRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.features.ui.account.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single { AuthJwtHerokuClient(get(named("accountHerokuClient"))) }
    single { AuthRemoteDataSource(get()) }
    single { AuthLocalDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single { AuthUseCase(get()) }
    viewModel { AuthViewModel(get()) }
}