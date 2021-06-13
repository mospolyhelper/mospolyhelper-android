package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface ScheduleRepository {
    fun getSchedule(user: UserSchedule): Flow<Result0<Schedule>>
    suspend fun getScheduleVersion(user: UserSchedule): ScheduleVersionDb?
    suspend fun getSchedulePackList(onProgressChanged: (Float) -> Unit): SchedulePackList
    suspend fun getSchedulePackListLocal(): Result0<SchedulePackList>
    suspend fun updateSchedule(user: UserSchedule?)
    val dataLastUpdatedObservable: Flow<ZonedDateTime>
}