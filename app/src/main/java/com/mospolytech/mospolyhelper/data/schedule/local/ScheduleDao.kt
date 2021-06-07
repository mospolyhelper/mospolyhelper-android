package com.mospolytech.mospolyhelper.data.schedule.local

import androidx.room.*
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule

@Dao
abstract class ScheduleDao {
    @Query("SELECT * FROM ScheduleVersionDb WHERE userScheduleId = :userScheduleId")
    abstract suspend fun getScheduleVersion(userScheduleId: String): ScheduleVersionDb?

    @Transaction
    open suspend fun getScheduleVersion(userSchedule: UserSchedule): ScheduleVersionDb? {
        return try {
            return getScheduleVersion(userSchedule.idGlobal)
        } catch (e: Exception) {
            null
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun setScheduleVersion(scheduleDb: ScheduleVersionDb)
}