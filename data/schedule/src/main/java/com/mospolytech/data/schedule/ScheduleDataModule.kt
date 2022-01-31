package com.mospolytech.data.schedule

import com.mospolytech.data.base.consts.DiConst
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.data.schedule.local.ScheduleLocalDS
import com.mospolytech.data.schedule.repository.ScheduleRepositoryImpl
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val scheduleDataModule = module {
    single { get<Retrofit>(named(DiConst.Schedule)).create(ScheduleService::class.java) }
    single { ScheduleLocalDS(get()) }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get(), get()) }
}