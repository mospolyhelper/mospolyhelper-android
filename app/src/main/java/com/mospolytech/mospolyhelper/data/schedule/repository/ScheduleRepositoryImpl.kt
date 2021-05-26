package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleDao
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleDb
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.data.utils.toLessonWithFeaturesDb
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.utils.Result2
import com.mospolytech.mospolyhelper.utils.getOrDefault
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger

class ScheduleRepositoryImpl(
    private val remoteDataSource: ScheduleRemoteDataSource,
    private val scheduleDao: ScheduleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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

    private val changesFlow = MutableSharedFlow<Schedule?>(extraBufferCapacity = 64)

    override fun getSchedule(
        user: UserSchedule?
    ) = flow {
        if (user == null) {
            emit(null)
        } else {
            val scheduleDb = scheduleDao.getScheduleByUser(user)
            when {
                user is AdvancedSearchSchedule -> emit(scheduleDb?.schedule?.filter(user.filters))
                scheduleDb?.schedule == null ||
                        scheduleDb.downloadingDateTime
                            .until(ZonedDateTime.now(), ChronoUnit.DAYS) >= 1 -> {
                    emit(refresh(user).getOrNull())
                }
                else -> {
                    emit(scheduleDb.schedule)
                }
            }
        }
        emitAll(changesFlow)
    }.flowOn(ioDispatcher)

    override suspend fun updateSchedule(user: UserSchedule?) = withContext(ioDispatcher) {
        if (user != null && user !is AdvancedSearchSchedule) {
            changesFlow.emit(refresh(user).getOrNull())
        }
    }


    private suspend fun refresh(user: UserSchedule): Result2<Schedule> = coroutineScope {
        val schedule = when (user) {
            is StudentSchedule -> remoteDataSource.getByGroup(user.id)
            is TeacherSchedule -> remoteDataSource.getByTeacher(user.id)
            else -> Result2.failure<Schedule>(Exception())
        }.onSuccess {
            runBlocking {
                scheduleDao.setSchedule(ScheduleDb(user.idGlobal, it, ZonedDateTime.now()))
            }
        }
        schedule
    }

    override suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        var counter1 = 0f
        var counter2 = 0f

        val lessonTitles = HashSet<String>(2000)
        val lessonTypes = HashSet<String>(30)
        val lessonTeachers = HashSet<String>(1000)
        val lessonGroups = HashSet<String>(500)
        val lessonAuditoriums = HashSet<String>(600)


        val schedule = withContext(ioDispatcher) {
            remoteDataSource
                .getAll(
                    lessonTitles,
                    lessonTypes,
                    lessonTeachers,
                    lessonGroups,
                    lessonAuditoriums)  {
                    counter2 = it
                    onProgressChanged((counter1 + counter2) / 2)
                }.getOrNull()
        }
        val downloadDateTime = ZonedDateTime.now()

        scheduleDao.setSchedule(ScheduleDb(UserSchedule.PREFIX_ADVANCED_SEARCH, schedule, downloadDateTime))
        return@coroutineScope SchedulePackList(
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