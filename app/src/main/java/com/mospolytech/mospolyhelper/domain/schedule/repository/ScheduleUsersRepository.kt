package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import kotlinx.coroutines.flow.Flow

interface ScheduleUsersRepository {
    fun getSavedUsers(): Flow<List<ScheduleSource>>
    suspend fun setSavedUsers(savedSources: List<ScheduleSource>)
    suspend fun addSavedUser(source: ScheduleSource)
    suspend fun removeSavedUser(source: ScheduleSource)

    fun getScheduleUsers(): Flow<List<ScheduleSource>>

    fun getCurrentUser(): Flow<ScheduleSource?>
    suspend fun setCurrentUser(source: ScheduleSource?)
}