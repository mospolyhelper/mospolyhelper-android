package com.mospolytech.mospolyhelper.data.schedule.local

import android.util.Log
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.TAG
import java.time.format.DateTimeFormatter

class ScheduleLocalDataSource(
    private val localConverter: ScheduleLocalConverter
) {

    companion object {
        const val SCHEDULE_FOLDER = "cached_schedules"
        const val SCHEDULE_STUDENT_FOLDER = "student"
        const val SCHEDULE_TEACHER_FOLDER = "teacher"
    }

    fun get(id: String, isStudent: Boolean): Schedule? {
        val file = App.context!!.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(if (isStudent) SCHEDULE_STUDENT_FOLDER else SCHEDULE_TEACHER_FOLDER)
            .resolve(id)

        if (!file.exists()) {
            return null
        }
        return try {
            localConverter.deserializeSchedule(file.readText())
        } catch (e: Exception) {
            Log.e(TAG, "Schedule reading and converting exception", e)
            null
        }
    }

    fun set(schedule: Schedule, id: String, isStudent: Boolean) {
        val file = App.context!!.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(if (isStudent) SCHEDULE_STUDENT_FOLDER else SCHEDULE_TEACHER_FOLDER)
            .resolve(id)
        if (file.exists()) {
            file.delete()
        } else {
            file.parentFile?.mkdirs()
        }
        try {
            file.createNewFile()
            file.writeText(localConverter.serializeSchedule(schedule))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule converting and writing exception", e)
        }
    }
}