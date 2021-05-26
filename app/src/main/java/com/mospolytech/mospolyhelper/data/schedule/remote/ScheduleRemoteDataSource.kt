package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleFullRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import com.mospolytech.mospolyhelper.utils.Result2
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class ScheduleRemoteDataSource(
    private val client: ScheduleClient,
    private val scheduleGroupParser: ScheduleRemoteConverter,
    private val scheduleFullParser: ScheduleFullRemoteConverter,
    private val scheduleTeacherParser: ScheduleTeacherRemoteConverter
) {

    private suspend fun getByGroup(groupTitle: String, isSession: Boolean): Result2<Schedule> {
        return try {
            val scheduleString = client.getScheduleByGroup(groupTitle, isSession)
            Result2.success(scheduleGroupParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing exception: groupTitle: $groupTitle, isSession: $isSession", e)
            Result2.failure(e)
        }
    }

    suspend fun getByGroup(groupId: String): Result2<Schedule> = coroutineScope {
        val regularDeferred = async { getByGroup(groupId, false).getOrNull() }
        val sessionDeferred = async { getByGroup(groupId, true).getOrNull() }
        val regular = regularDeferred.await()
        val session = sessionDeferred.await()
        return@coroutineScope if (regular != null) {
            if (session != null) {
                Result2.success(combine(regular, session))
            } else {
                Result2.success(regular)
            }
        } else {
            if (session != null) {
                Result2.success(session)
            } else {
                Result2.failure(Exception())
            }
        }
    }

    suspend fun getByTeacher(teacherId: String): Result2<Schedule> {
        return try {
            val scheduleString = client.getScheduleByTeacher(teacherId)
            Result2.success(scheduleTeacherParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: teacherId: $teacherId", e)
            Result2.failure(e)
        }
    }

    suspend fun getAll(
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        groupCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>,
        onProgress: (Float) -> Unit = { }
    ): Result2<Schedule?> = coroutineScope {
        try {
            val semesterDeferred = async { client.getSchedules(false, onProgress) }
            val sessionDeferred = async { client.getSchedules(false, onProgress) }

            Result2.success(
                scheduleFullParser.parseSchedules(
                    semesterDeferred.await(),
                    sessionDeferred.await(),
                    titleCollection,
                    typeCollection,
                    teacherCollection,
                    groupCollection,
                    auditoriumCollection
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing exception", e)
            Result2.failure(e)
        }
    }
}