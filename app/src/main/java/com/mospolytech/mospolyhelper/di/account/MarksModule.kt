package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.marks.repository.MarksRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.features.ui.account.marks.MarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val marksModule = module {
    single { MarksHerokuClient(get(named("accountHerokuClient"))) }
    single { MarksRemoteDataSource(get()) }
    single<MarksRepository> { MarksRepositoryImpl(get(), get()) }
    single { MarksUseCase(get()) }
    viewModel { MarksViewModel(get(), get()) }
}