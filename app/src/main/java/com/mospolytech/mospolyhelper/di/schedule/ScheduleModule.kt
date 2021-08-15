package com.mospolytech.mospolyhelper.di.schedule

import com.mospolytech.mospolyhelper.data.core.local.AppDatabase
import com.mospolytech.mospolyhelper.data.schedule.api.*
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleFullRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.local.LessonTagsLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.*
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonTagsRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.mospolyhelper.data.schedule.repository.ScheduleSourcesRepositoryImpl
import com.mospolytech.mospolyhelper.di.utils.DiConstants
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleSourcesRepository
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search.AdvancedSearchViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.ids.ScheduleIdsViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag.LessonTagViewModel
import io.ktor.client.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scheduleModule = module {

    single(named(DiConstants.SCHEDULE_CLIENT)) {
        HttpClient()
    }

    // Apis
    single { ScheduleClient(get(named(DiConstants.SCHEDULE_CLIENT))) }
    single { GroupListClient(get(named(DiConstants.SCHEDULE_CLIENT))) }
    single { GroupInfoApi(get(named(DiConstants.SCHEDULE_CLIENT))) }
    single { TeacherInfoApi(get(named(DiConstants.SCHEDULE_CLIENT))) }
    single { AuditoriumInfoApi(get(named(DiConstants.SCHEDULE_CLIENT))) }

    // Converters
    single { ScheduleRemoteConverter() }
    single { ScheduleFullRemoteConverter() }
    single { ScheduleTeacherRemoteConverter() }
    single { GroupListRemoteConverter() }

    // DataSources
    single { ScheduleRemoteDataSource(get(), get(), get(), get()) }
    single { ScheduleLocalDataSource(get()) }
    single { GroupListRemoteDataSource(get(), get()) }
    single { TeacherListRemoteDataSource() }
    single { LessonTagsLocalDataSource(get()) }
    single { GroupInfoRemoteDataSource(get()) }
    single { TeacherInfoRemoteDataSource(get()) }
    single { AuditoriumInfoRemoteDataSource(get()) }

    single { get<AppDatabase>().getScheduleDao() }

    // Repositories
    single<ScheduleRepository> {
        ScheduleRepositoryImpl(get(), get(), get())
    }
    single<LessonTagsRepository> {
        LessonTagsRepositoryImpl(get())
    }
    single<ScheduleSourcesRepository> {
        ScheduleSourcesRepositoryImpl(get(), get(), get())
    }

    // UseCases
    single { ScheduleUseCase(get(), get(), get(), get(), get()) }

    // ViewModels
    viewModel { ScheduleViewModel(get()) }
    viewModel { AdvancedSearchViewModel(get(), get()) }
    viewModel { LessonInfoViewModel(get(), get(), get()) }
    viewModel { LessonTagViewModel(get()) }
    viewModel { ScheduleIdsViewModel(get()) }
}