package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.combine
import com.mospolytech.mospolyhelper.utils.Result2
import com.mospolytech.mospolyhelper.utils.getOrDefault
import com.mospolytech.mospolyhelper.utils.getOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.lang.Exception
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
                refresh(user).getOrNull() ?: localDataSource.get(user)
            } else {
                localDataSource.get(user) ?: refresh(user).getOrNull()
            }
            emit(schedule)
        }
    }

    private suspend fun refresh(user: UserSchedule): Result2<Schedule> {
        val schedule = when (user) {
            is StudentSchedule -> remoteDataSource.getByGroup(user.id)
            is TeacherSchedule -> remoteDataSource.getByTeacher(user.id)
            is AuditoriumSchedule -> Result2.Failure(Exception())
        }
        if (schedule is Result2.Success) {
            localDataSource.set(schedule.value, user)
        }
        return schedule
    }


    override suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        scheduleCounter.set(0)

        val schedules = remoteDataSource
            .getAll(false, onProgressChanged)
            .getOrDefault(emptySequence()) +
                remoteDataSource
                    .getAll(true, onProgressChanged)
                    .getOrDefault(emptySequence())

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