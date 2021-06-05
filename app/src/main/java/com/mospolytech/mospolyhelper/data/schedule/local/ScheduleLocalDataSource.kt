package com.mospolytech.mospolyhelper.data.schedule.local

import android.content.Context
import android.util.Log
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ScheduleLocalDataSource(
    private val context: Context
) {
    companion object {
        const val SCHEDULE_FOLDER = "cached_schedules"
    }

    fun get(userScheduleGlobalId: String): Result0<Schedule> {
        val file = context.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(userScheduleGlobalId)

        if (!file.exists()) {
            return Result0.Failure(Exception("Schedule not found"))
        }
        return try {
            val json = file.readText()
            if (json.isEmpty()) return Result0.Failure(Exception("Schedule not found"))
            Result0.Success(Json.decodeFromString<Schedule>(json))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule reading and converting exception", e)
            Result0.Failure(e)
        }
    }

    fun set(schedule: Schedule?, userScheduleGlobalId: String) {
        Log.d(TAG, "Saving Schedule")
        val file = context.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(userScheduleGlobalId)
        if (file.exists()) {
            file.delete()
        } else {
            file.parentFile?.mkdirs()
        }
        try {
            file.createNewFile()
            file.writeText(if (schedule == null) "" else Json.encodeToString(schedule))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule converting and writing exception", e)
        }
    }
}