package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import org.koin.dsl.module

val accountUseCaseModule = module {
    single { AuthUseCase(get()) }
}