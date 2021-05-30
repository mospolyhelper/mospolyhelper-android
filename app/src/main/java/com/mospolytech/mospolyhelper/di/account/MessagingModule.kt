package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.messaging.api.MessagingHerokuClient
import com.mospolytech.mospolyhelper.data.account.messaging.local.MessagingLocalDataSource
import com.mospolytech.mospolyhelper.data.account.messaging.remote.MessagingRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.messaging.repository.MessagingRepositoryImplementation
import com.mospolytech.mospolyhelper.domain.account.messaging.repository.MessagingRepository
import com.mospolytech.mospolyhelper.domain.account.messaging.usecase.MessagingUseCase
import com.mospolytech.mospolyhelper.features.ui.account.messaging.MessagingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val messagingModule = module {
    single { MessagingHerokuClient(get(named("accountHerokuClient"))) }
    single { MessagingRemoteDataSource(get()) }
    single { MessagingLocalDataSource(get()) }
    single<MessagingRepository> { MessagingRepositoryImplementation(get(), get(), get(), get()) }
    single { MessagingUseCase(get()) }
    viewModel { MessagingViewModel(get(), get()) }
}