package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteTeacherDataSource
import com.mospolytech.mospolyhelper.data.schedule.utils.ScheduleIterable
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicInteger

class ScheduleRepositoryImpl(
    private val localDataSource: ScheduleLocalDataSource,
    private val remoteDataSource: ScheduleRemoteDataSource,
    private val remoteTeacherDataSource: ScheduleRemoteTeacherDataSource
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
                        lessonTeachers.add(teacher.name)
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
        user: UserSchedule?,
        refresh: Boolean
    ) = flow {
        if (user == null) {
            emit(null)
        } else {
            val schedule = if (refresh) {
                refresh(user) ?: localDataSource.get(user)
            } else {
                localDataSource.get(user) ?: refresh(user)
            }
            emit(schedule)
        }
    }

    private suspend fun refresh(user: UserSchedule): Schedule? {
        val schedule = when (user) {
            is StudentSchedule -> combine(
                remoteDataSource.get(user.id, false),
                remoteDataSource.get(user.id, true)
            )
            is TeacherSchedule -> remoteTeacherDataSource.get(user.id)
            is AuditoriumSchedule -> null
        }
        if (schedule != null) {
            localDataSource.set(schedule, user)
        }
        return schedule
    }

//    override suspend fun getAnySchedules(ids: List<String>, isStudent: Boolean, onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
//        if (ids.isEmpty()) {
//            return@coroutineScope SchedulePackList(
//                emptyList(),
//                mutableSetOf(),
//                mutableSetOf(),
//                mutableSetOf(),
//                mutableSetOf()
//            )
//        }
//
//        scheduleCounter.set(0)
//        val maxProgress = ids.size * 2
//
//        val chunkSize = ids.size / (Runtime.getRuntime().availableProcessors() * 3)
//        val chunks = if (chunkSize > 3) ids.chunked(chunkSize) else listOf(ids)
//
//        val channel = Channel<Schedule?>()
//        val deferredList = chunks.map { chunk ->
//            async(context = Dispatchers.IO) {
//                for (groupTitle in chunk) {
//                    channel.send(refresh(groupTitle, isStudent))
//                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
//                }
//            }
//        }
//
//        launch {
//            deferredList.awaitAll()
//            channel.close()
//        }
//
//        val packList =
//            SchedulePackList(
//                ScheduleIterable(
//                    ids
//                ),
//                sortedSetOf(),
//                sortedSetOf(),
//                sortedSetOf(),
//                sortedSetOf()
//            )
//        channel.receiveAsFlow().collect {
//            if (it == null) {
//                return@collect
//            }
//            for (dailySchedule in it.dailySchedules) {
//                for (lesson in dailySchedule) {
//                    packList.lessonTitles.add(lesson.title)
//                    for (teacher in lesson.teachers) {
//                        packList.lessonTeachers.add(teacher.name)
//                    }
//                    for (auditorium in lesson.auditoriums) {
//                        packList.lessonAuditoriums.add(auditorium.title)
//                    }
//                    packList.lessonTypes.add(lesson.type)
//                }
//            }
//            onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
//        }
//        return@coroutineScope packList
//    }

    override suspend fun getAnySchedules(ids: List<String>, isStudent: Boolean, onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        if (ids.isEmpty()) {
            return@coroutineScope SchedulePackList(
                emptyList(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf()
            )
        }

        scheduleCounter.set(0)
        val maxProgress = 2

        val packList =
            SchedulePackList(
                ScheduleIterable(
                    ids
                ),
                sortedSetOf(),
                sortedSetOf(),
                sortedSetOf(),
                sortedSetOf()
            )
        val schedules = remoteDataSource.getAll(false) { c, t -> onProgressChanged(c.toFloat() / t)} +
                remoteDataSource.getAll(true) { c, t -> onProgressChanged(c.toFloat() / t)}

        for (schedule in schedules) {
            for (dailySchedule in schedule.dailySchedules) {
                for (lesson in dailySchedule) {
                    packList.lessonTitles.add(lesson.title)
                    for (teacher in lesson.teachers) {
                        packList.lessonTeachers.add(teacher.name)
                    }
                    for (auditorium in lesson.auditoriums) {
                        packList.lessonAuditoriums.add(auditorium.title)
                    }
                    packList.lessonTypes.add(lesson.type)
                }
            }

        }
        onProgressChanged(1f)
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