package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.features.ui.account.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
//    single { AuthHerokuClient(get(named("accountHerokuClient"))) }
//    single { AuthJwtHerokuClient(get(named("accountHerokuClient"))) }
//    single { AuthJwtRemoteDataSource(get()) }
//    single { AuthJwtLocalDataSource(get()) }
//    single { AuthRemoteDataSource(get()) }
//    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
//    single { AuthUseCase(get()) }
    viewModel { AuthViewModel(get(), get()) }
}