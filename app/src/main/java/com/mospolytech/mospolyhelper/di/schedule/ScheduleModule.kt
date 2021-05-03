package com.mospolytech.mospolyhelper.di.schedule

import com.mospolytech.mospolyhelper.data.schedule.api.*
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.local.*
import com.mospolytech.mospolyhelper.data.schedule.remote.*
import com.mospolytech.mospolyhelper.data.schedule.repository.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.*
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.ids.ScheduleIdsViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag.LessonTagViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scheduleModule = module {

    // Apis
    single { ScheduleClient(get(named("schedule"))) }
    single { GroupListClient(get(named("schedule"))) }
    single { GroupInfoApi(get(named("schedule"))) }
    single { TeacherInfoApi(get(named("schedule"))) }
    single { AuditoriumInfoApi(get(named("schedule"))) }

    // Converters
    single { ScheduleRemoteConverter() }
    single { ScheduleTeacherRemoteConverter() }
    single { GroupListRemoteConverter() }

    // DataSources
    single { ScheduleRemoteDataSource(get(), get()) }
    single { ScheduleRemoteTeacherDataSource(get(), get()) }
    single { ScheduleLocalDataSource() }
    single { GroupListLocalDataSource() }
    single { GroupListRemoteDataSource(get(), get()) }
    single { TeacherListLocalDataSource() }
    single { TeacherListRemoteDataSource() }
    single { LessonTagsLocalDataSource(get()) }
    single { SavedIdsLocalDataSource(get()) }
    single { GroupInfoRemoteDataSource(get()) }
    single { TeacherInfoRemoteDataSource(get()) }
    single { AuditoriumInfoRemoteDataSource(get()) }

    // Repositories
    single<ScheduleRepository> {
        ScheduleRepositoryImpl(get(), get(), get())
    }
    single<LessonTagsRepository> {
        LessonTagsRepositoryImpl(get())
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
    viewModel { LessonTagViewModel(get()) }
    viewModel { ScheduleIdsViewModel(get(), get()) }
}