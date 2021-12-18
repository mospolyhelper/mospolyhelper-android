package com.mospolytech.domain.schedule

import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import org.koin.dsl.module

val scheduleDomainModule = module {
    single { ScheduleUseCase(get()) }
}