package com.mospolytech.data.schedule.repository

import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.data.schedule.api.ScheduleService
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.model.ScheduleSource
import com.mospolytech.domain.schedule.model.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScheduleRepositoryImpl(
    private val service: ScheduleService
) : ScheduleRepository {
    override fun getSources(type: ScheduleSources) = flow {
        emit(service.getSources(type.name.lowercase()).toResult())
    }

    override fun getSchedule(source: ScheduleSource) = flow {
        emit(service.getSchedule(source.type.name.lowercase(), source.key).toResult())
    }
}