package com.mospolytech.mospolyhelper.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.addresses.AddressesDao
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.local.AppDatabase
import com.mospolytech.mospolyhelper.repository.schedule.GroupListDao
import com.mospolytech.mospolyhelper.repository.schedule.GroupListRepository
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleDao
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleRepository
import com.mospolytech.mospolyhelper.ui.addresses.AddressesViewModel
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.deadlines.DeadlineViewModel
import com.mospolytech.mospolyhelper.ui.deadlines.bottomdialog.DialogFragmentViewModel
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.ui.schedule.advanced_search.AdvancedSearchViewModel
import com.mospolytech.mospolyhelper.ui.schedule.calendar.CalendarViewModel
import com.mospolytech.mospolyhelper.ui.schedule.lesson_info.LessonInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Mediator<String, ViewModelMessage>> { Mediator<String, ViewModelMessage>() }

    single<AddressesDao> { AddressesDao() }
    single<ScheduleDao> { ScheduleDao() }
    single<GroupListDao> { GroupListDao() }

    single {
        Room.databaseBuilder(
        App.context,
        AppDatabase::class.java, "database")
        .build() }

    single<ScheduleRepository> { ScheduleRepository(get()) }
    single<GroupListRepository> { GroupListRepository(get()) }
    single<DeadlinesRepository> { DeadlinesRepository(get()) }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

    viewModel<AddressesViewModel> { AddressesViewModel(get(), get()) }
    viewModel<DeadlineViewModel> { DeadlineViewModel(get(), get()) }
    viewModel<DialogFragmentViewModel> { DialogFragmentViewModel(get(), get()) }

    viewModel<ScheduleViewModel> { ScheduleViewModel.Factory.create(get(), get(), get(), get()) }
    viewModel<AdvancedSearchViewModel> { AdvancedSearchViewModel(get(), get(), get()) }
    viewModel<LessonInfoViewModel> { LessonInfoViewModel(get(), get()) }
    viewModel<CalendarViewModel> { CalendarViewModel(get()) }
}