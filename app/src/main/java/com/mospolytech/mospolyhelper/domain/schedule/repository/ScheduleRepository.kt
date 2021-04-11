package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedule(
        user: UserSchedule?,
        refresh: Boolean
    ): Flow<Schedule?>

    suspend fun getAnySchedules(ids: List<String>, isStudent: Boolean, onProgressChanged: (Float) -> Unit): SchedulePackList
}