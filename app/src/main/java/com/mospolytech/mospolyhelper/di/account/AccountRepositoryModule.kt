package com.mospolytech.mospolyhelper.di.account

import com.mospolytech.mospolyhelper.data.account.repository.ClassmatesRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.DeadlinesRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.DialogsRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.GroupMarksRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.InfoRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.MarksRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.MessagingRepositoryImpl
import com.mospolytech.mospolyhelper.data.account.repository.*
import com.mospolytech.mospolyhelper.domain.account.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.domain.account.repository.AuthRepository
import com.mospolytech.mospolyhelper.domain.account.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.domain.account.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.account.repository.DialogsRepository
import com.mospolytech.mospolyhelper.domain.account.repository.GroupMarksRepository
import com.mospolytech.mospolyhelper.domain.account.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.repository.MessagingRepository
import com.mospolytech.mospolyhelper.domain.account.repository.PaymentsRepository
import com.mospolytech.mospolyhelper.domain.account.repository.StatementsRepository
import com.mospolytech.mospolyhelper.domain.account.repository.StudentsRepository
import com.mospolytech.mospolyhelper.domain.account.repository.TeachersRepository
import org.koin.dsl.module

val accountRepositoryModule = module {
    single<ApplicationsRepository> { ApplicationsRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<TeachersRepository> { TeachersRepositoryImpl(get(), get()) }
    single<StudentsRepository> { StudentsRepositoryImpl(get()) }
    single<StatementsRepository> { StatementsRepositoryImpl(get(), get()) }
    single<PaymentsRepository> { PaymentsRepositoryImpl(get(), get()) }
    single<MessagingRepository> { MessagingRepositoryImpl(get(), get()) }
    single<MarksRepository> { MarksRepositoryImpl(get(), get()) }
    single<InfoRepository> { InfoRepositoryImpl(get(), get()) }
    single<GroupMarksRepository> { GroupMarksRepositoryImpl(get(), get()) }
    single<DialogsRepository> { DialogsRepositoryImpl(get(), get()) }
    single<ClassmatesRepository> { ClassmatesRepositoryImpl(get(), get()) }
    single<DeadlinesRepository> { DeadlinesRepositoryImpl(get(), get()) }
}