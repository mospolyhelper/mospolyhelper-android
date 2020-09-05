package com.mospolytech.mospolyhelper.data.schedule.repository

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.data.schedule.utils.ScheduleIterable
import com.mospolytech.mospolyhelper.domain.schedule.model.SchedulePackList
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicInteger

class ScheduleRepositoryImpl(
    private val localDataSource: ScheduleLocalDataSource,
    private val remoteDataSource: ScheduleRemoteDataSource
) : ScheduleRepository {
    companion object {
        fun allDataFromSchedule(schedule: Schedule): SchedulePack {
            val lessonTitles = HashSet<String>()
            val lessonTeachers = HashSet<String>()
            val lessonAuditoriums = HashSet<String>()
            val lessonTypes = HashSet<String>()
            for (dailySchedule in schedule.dailySchedules) {
                for (lesson in dailySchedule) {
                    lessonTitles.add(lesson.title)
                    for (teacher in lesson.teachers) {
                        lessonTeachers.add(teacher.getFullName())
                    }
                    if (lesson.auditoriums.isNotEmpty()) {
                        for (auditorium in lesson.auditoriums) {
                            lessonAuditoriums.add(auditorium.title)
                        }
                    }
                    lessonTypes.add(lesson.type)
                }
            }
            return SchedulePack(
                schedule,
                lessonTitles,
                lessonTeachers,
                lessonAuditoriums,
                lessonTypes
            )
        }
    }

    private val scheduleCounter = AtomicInteger(0)

    override fun getSchedule(
        group: String,
        isSession: Boolean,
        refresh: Boolean
    ) = flow<Schedule?> {
        var schedule: Schedule? = null
        if (refresh) {
            try {
                schedule = refresh(group, isSession)
            } catch (e1: Exception) {
                Log.e(TAG, "Download schedule fail", e1)
            }
        } else {
            try {
                schedule = localDataSource.get(group, isSession)
            } catch (e: Exception) {
                Log.e(TAG, "Read schedule error", e)
            }
        }
        if (schedule == null) {
            if (!refresh) {
                try {
                    schedule = refresh(group, isSession)
                } catch (e1: Exception) {
                    Log.e(TAG, "Download schedule fail", e1)
                }
            } else {
                try {
                    schedule = localDataSource.get(group, isSession)
                } catch (e: Exception) {
                    Log.e(TAG, "Read schedule error", e)
                }
            }
        }
        emit(schedule)
    }

    private suspend fun refresh(group: String, isSession: Boolean): Schedule? {
        val schedule = remoteDataSource.get(group, isSession)
        if (schedule != null) {
            try {
                localDataSource.set(schedule)
            } catch (e: Exception) {
            }
        }
        return schedule
    }

    override suspend fun getAnySchedules(groupList: List<String>, onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        if (groupList.isEmpty()) {
            return@coroutineScope SchedulePackList(
                emptyList(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf()
            )
        }

        scheduleCounter.set(0)
        val maxProgress = groupList.size * 4

        val chunkSize = groupList.size / (Runtime.getRuntime().availableProcessors() * 3)
        val chunks = if (chunkSize > 3) groupList.chunked(chunkSize) else listOf(groupList)

        val channel = Channel<Schedule?>()
        val deferredList = chunks.map { chunk ->
            async(context = Dispatchers.IO) {
                for (groupTitle in chunk) {
                    channel.send(refresh(groupTitle, false))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
                for (groupTitle in chunk) {
                    channel.send(refresh(groupTitle, true))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
            }
        }

        launch {
            deferredList.awaitAll()
            channel.close()
        }

        val packList =
            SchedulePackList(
                ScheduleIterable(
                    groupList
                ),
                sortedSetOf(),
                sortedSetOf(),
                sortedSetOf(),
                sortedSetOf()
            )
        channel.receiveAsFlow().collect {
            onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
            if (it == null) {
                return@collect
            }
            for (dailySchedule in it.dailySchedules) {
                for (lesson in dailySchedule) {
                    packList.lessonTitles.add(lesson.title)
                    for (teacher in lesson.teachers) {
                        packList.lessonTeachers.add(teacher.getFullName())
                    }
                    for (auditorium in lesson.auditoriums) {
                        packList.lessonAuditoriums.add(auditorium.title)
                    }
                    packList.lessonTypes.add(lesson.type)
                }
            }
        }
        return@coroutineScope packList
    }



    class SchedulePack(
        val schedule: Schedule,
        val lessonTitles: Set<String>,
        val lessonTeachers: Set<String>,
        val lessonAuditoriums: Set<String>,
        val lessonTypes: Set<String>
    )
}