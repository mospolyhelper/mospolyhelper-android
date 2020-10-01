package com.mospolytech.mospolyhelper.di.schedule

import com.mospolytech.mospolyhelper.data.schedule.api.GroupListClient
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.local.*
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteTeacherDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.repository.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.SavedIdsRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.TeacherListRepository
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.ids.ScheduleIdsViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scheduleModule = module {

    // Apis
    single { ScheduleClient() }
    single { GroupListClient() }

    // Converters
    single { ScheduleLocalConverter() }
    single { ScheduleRemoteConverter() }
    single { ScheduleTeacherRemoteConverter() }
    single { GroupListRemoteConverter() }

    // DataSources
    single { ScheduleRemoteDataSource(get(), get()) }
    single { ScheduleRemoteTeacherDataSource(get(), get()) }
    single { ScheduleLocalDataSource(get()) }
    single { GroupListLocalDataSource() }
    single { GroupListRemoteDataSource(get(), get()) }
    single { TeacherListLocalDataSource() }
    single { TeacherListRemoteDataSource() }
    single { LessonLabelLocalDataSource() }
    single { SavedIdsLocalDataSource(get()) }

    // Repositories
    single<ScheduleRepository> {
        ScheduleRepositoryImpl(get(), get(), get())
    }
    single<LessonLabelRepository> {
        LessonLabelRepository(get())
    }
    single<GroupListRepository> {
        GroupListRepositoryImpl(get(), get())
    }
    single<TeacherListRepository> {
        TeacherListRepositoryImpl(get(), get())
    }
    single<SavedIdsRepository> {
        SavedIdsRepositoryImpl(get())
    }

    // UseCases
    single { ScheduleUseCase(get(), get(), get(), get(), get(), get(), get()) }

    // ViewModels
    viewModel<ScheduleViewModel> { ScheduleViewModel(get(), get()) }
    viewModel<AdvancedSearchViewModel> { AdvancedSearchViewModel(get(), get(), get()) }
    viewModel<LessonInfoViewModel> { LessonInfoViewModel(get(), get(), get()) }
    viewModel { ScheduleIdsViewModel(get(), get()) }
}