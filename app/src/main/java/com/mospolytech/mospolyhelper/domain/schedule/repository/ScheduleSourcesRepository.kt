package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import kotlinx.coroutines.flow.Flow

interface ScheduleSourcesRepository {
    fun getFavoriteScheduleSources(): Flow<List<ScheduleSource>>
    suspend fun addFavoriteScheduleSource(source: ScheduleSource)
    suspend fun removeFavoriteScheduleSource(source: ScheduleSource)

    fun getScheduleSources(): Flow<List<ScheduleSource>>

    fun getSelectedScheduleSource(): Flow<ScheduleSource?>
    suspend fun setSelectedScheduleSource(source: ScheduleSource?)
}