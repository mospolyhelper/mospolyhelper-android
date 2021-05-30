package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.data.account.teachers.repository.TeachersRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.teachers.repository.TeachersRepository
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.features.ui.account.teachers.TeachersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val teachersModule = module {
    single { TeachersHerokuClient(get(named("accountHerokuClient"))) }
    single<TeachersRepository> { TeachersRepositoryImpl(get(), get()) }
    single { TeachersUseCase(get()) }
    viewModel { TeachersViewModel(get(), get()) }
}