package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.marks.local.MarksLocalDataSource
import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.marks.repository.MarksRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.students.remote.StudentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.students.repository.StudentsRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.data.account.teachers.repository.TeachersRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.domain.account.teachers.repository.TeachersRepository
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.features.ui.account.marks.MarksViewModel
import com.mospolytech.mospolyhelper.features.ui.account.students.StudentsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.teachers.TeachersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val teachersModule = module {
    single { TeachersHerokuClient(get(named("accountHerokuClient"))) }
    single<TeachersRepository> { TeachersRepositoryImpl(get(), get()) }
    single { TeachersUseCase(get()) }
    viewModel { TeachersViewModel(get(), get(), get()) }
}