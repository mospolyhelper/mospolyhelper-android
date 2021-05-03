package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteTeacherDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
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
                for (lessonPlace in dailySchedule) {
                    for (lesson in lessonPlace.lessons) {
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


    override suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        scheduleCounter.set(0)

        val schedules = remoteDataSource.getAll(false) { c, t -> onProgressChanged(c.toFloat() / t)} +
                remoteDataSource.getAll(true) { c, t -> onProgressChanged(c.toFloat() / t)}

        val scheduleList = mutableListOf<Schedule>()
        val lessonTitles = mutableSetOf<String>()
        val lessonTeachers = mutableSetOf<String>()
        val lessonGroups = mutableSetOf<String>()
        val lessonAuditoriums = mutableSetOf<String>()
        val lessonTypes = mutableSetOf<String>()


        for (schedule in schedules) {
            scheduleList.add(schedule)
            for (dailySchedule in schedule.dailySchedules) {
                for (lessonPlace in dailySchedule) {
                    for (lesson in lessonPlace.lessons) {
                        lessonTitles.add(lesson.title)
                        for (teacher in lesson.teachers) {
                            lessonTeachers.add(teacher.name)
                        }
                        for (group in lesson.groups) {
                            lessonGroups.add(group.title)
                        }
                        for (auditorium in lesson.auditoriums) {
                            lessonAuditoriums.add(auditorium.title)
                        }
                        lessonTypes.add(lesson.type)
                    }
                }
            }

        }
        onProgressChanged(1f)
        return@coroutineScope SchedulePackList(
            scheduleList,
            lessonTitles.sorted(),
            lessonTypes.sorted(),
            lessonTeachers.sorted(),
            lessonGroups.sorted(),
            lessonAuditoriums.sorted(),
        )
    }





    class SchedulePack(
        val schedule: Schedule,
        val lessonTitles: Set<String>,
        val lessonTeachers: Set<String>,
        val lessonAuditoriums: Set<String>,
        val lessonTypes: Set<String>
    )
}