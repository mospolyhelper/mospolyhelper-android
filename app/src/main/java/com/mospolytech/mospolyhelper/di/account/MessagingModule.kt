package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.account.info.local.InfoLocalDataSource
import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.info.repository.InfoRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.marks.local.MarksLocalDataSource
import com.mospolytech.mospolyhelper.data.account.marks.remote.MarksRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.marks.repository.MarksRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.messaging.api.MessagingHerokuClient
import com.mospolytech.mospolyhelper.data.account.messaging.local.MessagingLocalDataSource
import com.mospolytech.mospolyhelper.data.account.messaging.remote.MessagingRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.messaging.repository.MessagingRepositoryImplementation
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.students.remote.StudentsRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.students.repository.StudentsRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.data.account.teachers.repository.TeachersRepositoryImpl
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.marks.usecase.MarksUseCase
import com.mospolytech.mospolyhelper.domain.account.messaging.repository.MessagingRepository
import com.mospolytech.mospolyhelper.domain.account.messaging.usecase.MessagingUseCase
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import com.mospolytech.mospolyhelper.domain.account.students.usecase.StudentsUseCase
import com.mospolytech.mospolyhelper.domain.account.teachers.repository.TeachersRepository
import com.mospolytech.mospolyhelper.domain.account.teachers.usecase.TeachersUseCase
import com.mospolytech.mospolyhelper.features.ui.account.info.InfoViewModel
import com.mospolytech.mospolyhelper.features.ui.account.marks.MarksViewModel
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingViewModel
import com.mospolytech.mospolyhelper.features.ui.account.students.StudentsViewModel
import com.mospolytech.mospolyhelper.features.ui.account.teachers.TeachersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val messagingModule = module {
    single { MessagingHerokuClient(get(named("accountHerokuClient"))) }
    single { MessagingRemoteDataSource(get()) }
    single { MessagingLocalDataSource(get()) }
    single<MessagingRepository> { MessagingRepositoryImplementation(get(), get(), get()) }
    single { MessagingUseCase(get()) }
    viewModel { MessagingViewModel(get(), get()) }
}