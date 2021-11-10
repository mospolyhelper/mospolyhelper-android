package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.account.api.AccountApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val accountMainModule = module {
    single<AccountApi> { AccountApiImpl(get(named("accountHerokuClient")), get()) }
}