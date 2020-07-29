package com.mospolytech.mospolyhelper.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.addresses.AddressesDao
import com.mospolytech.mospolyhelper.repository.deadline.Deadline
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.local.AppDatabase
import com.mospolytech.mospolyhelper.repository.schedule.*
import com.mospolytech.mospolyhelper.ui.addresses.AddressesViewModel
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.main.MainViewModel
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
    single<LessonLabelDao> { LessonLabelDao() }
    single<GroupListDao> { GroupListDao() }

    single<AppDatabase> { AppDatabase.getDatabase(get()) }

    single<ScheduleRepository> { ScheduleRepository(get()) }
    single<LessonLabelRepository> { LessonLabelRepository(get()) }
    single<GroupListRepository> { GroupListRepository(get()) }
    single<DeadlinesRepository> { DeadlinesRepository(get()) }

    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }

    // Not viewModel but single type because MainViewModel is shared with MainMenuFragment
    single<MainViewModel> { MainViewModel() }

    viewModel<AddressesViewModel> { AddressesViewModel(get(), get()) }

    viewModel<ScheduleViewModel> { ScheduleViewModel.Factory.create(get(), get(), get(), get(), get(), get()) }
    viewModel<AdvancedSearchViewModel> { AdvancedSearchViewModel(get(), get(), get()) }
    viewModel<LessonInfoViewModel> { LessonInfoViewModel(get(), get(), get()) }
    viewModel<CalendarViewModel> { CalendarViewModel(get()) }
}