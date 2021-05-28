package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import com.mospolytech.mospolyhelper.utils.Result2
import com.mospolytech.mospolyhelper.utils.TAG
import com.mospolytech.mospolyhelper.utils.getOrNull


class ScheduleRemoteDataSource(
    private val client: ScheduleClient,
    private val scheduleGroupParser: ScheduleRemoteConverter,
    private val scheduleTeacherParser: ScheduleTeacherRemoteConverter
) {

    private suspend fun getByGroup(groupTitle: String, isSession: Boolean): Result2<Schedule> {
        return try {
            val scheduleString = client.getScheduleByGroup(groupTitle, isSession)
            Result2.Success(scheduleGroupParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing exception: groupTitle: $groupTitle, isSession: $isSession", e)
            Result2.Failure(e)
        }
    }

    suspend fun getByGroup(groupId: String): Result2<Schedule> {
        val regular = getByGroup(groupId, false).getOrNull()
        val session = getByGroup(groupId, true).getOrNull()
        return if (regular != null) {
            if (session != null) {
                Result2.Success(combine(regular, session))
            } else {
                Result2.Success(regular)
            }
        } else {
            if (session != null) {
                Result2.Success(session)
            } else {
                Result2.Failure(Exception())
            }
        }
    }

    suspend fun getByTeacher(teacherId: String): Result2<Schedule> {
        return try {
            val scheduleString = client.getScheduleByTeacher(teacherId)
            Result2.Success(scheduleTeacherParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: teacherId: $teacherId", e)
            Result2.Failure(e)
        }
    }

    suspend fun getAll(isSession: Boolean, onProgress: (Float) -> Unit = { }): Result2<Sequence<Schedule>> {
        return try {
            val scheduleString = client.getSchedules(isSession, onProgress)
            Result2.Success(scheduleGroupParser.parseSchedules(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing exception: groupTitle: isSession: $isSession", e)
            Result2.Failure(e)
        }
    }
}