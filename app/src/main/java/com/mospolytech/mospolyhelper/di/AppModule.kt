package com.mospolytech.mospolyhelper.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.addresses.AddressesDao
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.core.AppDatabase
import com.mospolytech.mospolyhelper.data.schedule.local.GroupListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.local.LessonLabelLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.repository.GroupListRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.features.ui.addresses.AddressesViewModel
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.main.MainViewModel
import com.mospolytech.mospolyhelper.features.ui.deadlines.DeadlineViewModel
import com.mospolytech.mospolyhelper.features.ui.deadlines.bottomdialog.DialogFragmentViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.calendar.CalendarViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
}