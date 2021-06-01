package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

interface ScheduleRepository {
    fun getSchedule(user: UserSchedule?): Flow<Result0<Schedule>>
    suspend fun getScheduleVersion(user: UserSchedule): ScheduleVersionDb?
    suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList
    suspend fun updateSchedule(user: UserSchedule?)
    val dataLastUpdatedObservable: Flow<ZonedDateTime>
}