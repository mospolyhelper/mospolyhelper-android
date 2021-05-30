package com.mospolytech.mospolyhelper.data.schedule.local

import android.content.Context
import android.util.Log
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ScheduleLocalDataSource(
    private val applicationContext: Context
) {
    companion object {
        const val SCHEDULE_FOLDER = "cached_schedules"
    }

    fun get(user: UserSchedule): Schedule? {
        val file = applicationContext.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(user.idGlobal)

        if (!file.exists()) {
            return null
        }
        return try {
            val json = file.readText()
            if (json.isEmpty()) return null
            Json.decodeFromString<Schedule>(json)
        } catch (e: Exception) {
            Log.e(TAG, "Schedule reading and converting exception", e)
            null
        }
    }

    fun set(schedule: Schedule?, userScheduleGlobalId: String) {
        Log.d(TAG, "Saving Schedule")
        val file = applicationContext.filesDir
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