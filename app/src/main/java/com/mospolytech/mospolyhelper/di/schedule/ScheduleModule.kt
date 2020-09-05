package com.mospolytech.mospolyhelper.di.schedule

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.addresses.AddressesDao
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.core.AppDatabase
import com.mospolytech.mospolyhelper.data.schedule.api.GroupListClient
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.local.GroupListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.local.LessonLabelLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.repository.GroupListRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
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
import kotlin.math.sin

val scheduleModule = module {

    // Apis
    single { ScheduleClient() }
    single { GroupListClient() }

    // Converters
    single { ScheduleLocalConverter() }
    single { ScheduleRemoteConverter() }
    single { GroupListRemoteConverter() }

    // DataSources
    single { ScheduleRemoteDataSource(get(), get()) }
    single { ScheduleLocalDataSource(get()) }
    single { GroupListLocalDataSource(get()) }
    single { GroupListRemoteDataSource(get(), get()) }
    single { LessonLabelLocalDataSource() }

    // Repositories
    single<ScheduleRepository> {
        ScheduleRepositoryImpl(get(), get())
    }
    single<LessonLabelRepository> {
        LessonLabelRepository(get())
    }
    single<GroupListRepository> {
        GroupListRepositoryImpl(get(), get())
    }

    // UseCases
    single { ScheduleUseCase(get(), get(), get(), get()) }

    // ViewModels
    viewModel<ScheduleViewModel> { ScheduleViewModel.Factory.create(get(), get(), get()) }
    viewModel<AdvancedSearchViewModel> { AdvancedSearchViewModel(get(), get(), get()) }
    viewModel<LessonInfoViewModel> { LessonInfoViewModel(get(), get(), get()) }
    viewModel<CalendarViewModel> { CalendarViewModel(get()) }
}