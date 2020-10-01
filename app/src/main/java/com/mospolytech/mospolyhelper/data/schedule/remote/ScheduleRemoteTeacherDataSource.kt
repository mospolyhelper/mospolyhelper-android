package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.TAG


class ScheduleRemoteTeacherDataSource(
    private val client: ScheduleClient,
    private val scheduleParser: ScheduleTeacherRemoteConverter
) {

    suspend fun get(teacherId: String): Schedule? {
        return try {
            val scheduleString = client.getScheduleByTeacher(teacherId)
            scheduleParser.parse(scheduleString)
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: teacherId: $teacherId", e)
            null
        }
    }
}