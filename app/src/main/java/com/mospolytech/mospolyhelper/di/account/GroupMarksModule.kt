package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.group_marks.api.GroupMarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.group_marks.remote.GroupMarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.group_marks.repository.GroupMarksRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.group_marks.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.domain.account.group_marks.usecase.GroupMarksUseCase
import com.mospolytech.mospolyhelper.features.ui.account.group_marks.GroupMarksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val groupMarksModule = module {
    single { GroupMarksHerokuClient(get(named("accountHerokuClient"))) }
    single { GroupMarksRemoteDataSource(get()) }
    single<GroupMarksRepository> { GroupMarksRepositoryImpl(get(), get()) }
    single { GroupMarksUseCase(get()) }
    viewModel { GroupMarksViewModel(get(), get()) }
}