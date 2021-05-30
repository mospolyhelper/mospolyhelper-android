package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import kotlinx.coroutines.flow.Flow

interface ScheduleUsersRepository {
    fun getSavedUsers(): Flow<List<UserSchedule>>
    suspend fun setSavedUsers(savedUsers: List<UserSchedule>)
    suspend fun addSavedUser(user: UserSchedule)
    suspend fun removeSavedUser(user: UserSchedule)

    fun getScheduleUsers(): Flow<List<UserSchedule>>

    fun getCurrentUser(): Flow<UserSchedule?>
    suspend fun setCurrentUser(user: UserSchedule?)
}