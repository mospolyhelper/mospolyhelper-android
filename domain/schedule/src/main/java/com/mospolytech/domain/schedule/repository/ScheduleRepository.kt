package com.mospolytech.domain.schedule.repository

import com.mospolytech.domain.schedule.model.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.ScheduleSources
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSources(type: ScheduleSources): Flow<Result<List<ScheduleSourceFull>>>
}