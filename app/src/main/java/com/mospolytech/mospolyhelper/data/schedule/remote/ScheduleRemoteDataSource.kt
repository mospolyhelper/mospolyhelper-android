package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleFullRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class ScheduleRemoteDataSource(
    private val client: ScheduleClient,
    private val scheduleGroupParser: ScheduleRemoteConverter,
    private val scheduleFullParser: ScheduleFullRemoteConverter,
    private val scheduleTeacherParser: ScheduleTeacherRemoteConverter
) {

    private suspend fun getByGroup(groupTitle: String, isSession: Boolean): Result0<Schedule> {
        return try {
            val scheduleString = client.getScheduleByGroup(groupTitle, isSession)
            Result0.Success(scheduleGroupParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing exception: groupTitle: $groupTitle, isSession: $isSession", e)
            Result0.Failure(e)
        }
    }

    suspend fun getByGroup(groupId: String): Result0<Schedule> = coroutineScope {
        val regularDeferred = async { getByGroup(groupId, false).getOrNull() }
        val sessionDeferred = async { getByGroup(groupId, true).getOrNull() }
        val regular = regularDeferred.await()
        val session = sessionDeferred.await()
        return@coroutineScope if (regular != null) {
            if (session != null) {
                Result0.Success(combine(regular, session))
            } else {
                Result0.Success(regular)
            }
        } else {
            if (session != null) {
                Result0.Success(session)
            } else {
                Result0.Failure(Exception())
            }
        }
    }

    suspend fun getByTeacher(teacherId: String): Result0<Schedule> {
        return try {
            val scheduleString = client.getScheduleByTeacher(teacherId)
            Result0.Success(scheduleTeacherParser.parse(scheduleString))
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: teacherId: $teacherId", e)
            Result0.Failure(e)
        }
    }

    suspend fun getAll(
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        groupCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>,
        onProgress: (Float) -> Unit
    ): Result0<Schedule> = coroutineScope {
        try {
            var counter1 = 0f
            var counter2 = 0f

            val semesterDeferred = async {
                client.getSchedules(false) {
                    counter1 = it
                    onProgress((counter1 + counter2) / 2)
                }
            }
            val sessionDeferred = async {
                client.getSchedules(true)  {
                    counter2 = it
                    onProgress((counter1 + counter2) / 2)
                }
            }

            Result0.Success(
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
            Result0.Failure(e)
        }
    }
}