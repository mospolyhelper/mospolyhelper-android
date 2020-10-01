package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedule(
        id: String,
        isStudent: Boolean,
        refresh: Boolean
    ): Flow<Schedule?>

    suspend fun getAnySchedules(ids: List<String>, isStudent: Boolean, onProgressChanged: (Float) -> Unit): SchedulePackList
}