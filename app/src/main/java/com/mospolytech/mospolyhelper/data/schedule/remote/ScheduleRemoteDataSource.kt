package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.utils.TAG


class ScheduleRemoteDataSource(
    private val client: ScheduleClient,
    private val scheduleParser: ScheduleRemoteConverter
) {

    suspend fun get(groupTitle: String, isSession: Boolean): Schedule? {
        return try {
            val scheduleString = client.getSchedule(groupTitle, isSession)
            scheduleParser.parse(scheduleString, isSession)
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: groupTitle: $groupTitle, isSession: $isSession", e)
            null
        }
    }
}