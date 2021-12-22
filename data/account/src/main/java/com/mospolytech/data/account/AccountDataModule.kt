package com.mospolytech.data.account

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.account.repository.*
import com.mospolytech.data.base.DiConst
import com.mospolytech.domain.account.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val accountDataModule = module {
    single { get<Retrofit>(named(DiConst.Account)).create(AccountService::class.java) }
    single<ApplicationsRepository> { ApplicationsRepositoryImpl(get()) }
    single<PaymentsRepository> { PaymentsRepositoryImpl(get()) }
    single<PeoplesRepository> { PeoplesRepositoryImpl(get()) }
    single<PerformanceRepository> { PerformanceRepositoryImpl(get()) }
    single<PersonalRepository> { PersonalRepositoryImpl(get()) }
}