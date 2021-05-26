package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ScheduleRepository {
    fun getSchedule(
        user: UserSchedule?
    ): Flow<Schedule?>

    suspend fun updateSchedule(user: UserSchedule?)

    suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList
}