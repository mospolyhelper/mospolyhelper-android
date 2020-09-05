package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedule(
        group: String,
        isSession: Boolean,
        refresh: Boolean
    ): Flow<Schedule?>

    suspend fun getAnySchedules(groupList: List<String>, onProgressChanged: (Float) -> Unit): SchedulePackList
}