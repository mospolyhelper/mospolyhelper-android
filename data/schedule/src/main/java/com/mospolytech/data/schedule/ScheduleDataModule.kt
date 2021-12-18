package com.mospolytech.data.schedule

import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val scheduleDataModule = module {
    single { get<Retrofit>().create(ScheduleService::class.java) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get()) }
}