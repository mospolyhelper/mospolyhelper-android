package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleDao
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.map
import com.mospolytech.mospolyhelper.utils.onFailure
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ScheduleRepositoryImpl(
    private val remoteDataSource: ScheduleRemoteDataSource,
    private val localDataSource: ScheduleLocalDataSource,
    private val scheduleDao: ScheduleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleRepository {

    private val dataLastUpdatedFlow = MutableSharedFlow<ZonedDateTime>(replay = 1)
    override val dataLastUpdatedObservable: Flow<ZonedDateTime> = dataLastUpdatedFlow

    override fun getSchedule(
        source: ScheduleSource
    ) = flow<Result0<Schedule>> {
        if (source is AdvancedSearchScheduleSource) {
            emit(localDataSource.get(source.idGlobal).map { it.filter(source.filters) })
        } else {
           val schedule = localDataSource.get(source.idGlobal)
            schedule.onSuccess {
               emit(schedule)
               val version = scheduleDao.getScheduleVersion(source)
               if (version == null ||
                   version.downloadingDateTime
                       .until(ZonedDateTime.now(), ChronoUnit.DAYS) >= 1
               ) {
                   emit(Result0.Loading)
                   emit(refresh(source))
               }
           }.onFailure {
               emit(refresh(source))
           }
        }
    }.flowOn(ioDispatcher)

    override suspend fun getScheduleVersion(source: ScheduleSource): ScheduleVersionDb? {
        return scheduleDao.getScheduleVersion(source)
    }

    override suspend fun updateSchedule(source: ScheduleSource?) = withContext(ioDispatcher) {
        if (source != null && source !is AdvancedSearchScheduleSource) {
            refresh(source)
            dataLastUpdatedFlow.emit(ZonedDateTime.now())
        }
    }

    private suspend fun refresh(source: ScheduleSource): Result0<Schedule> = coroutineScope {
        val schedule = when (source) {
            is StudentScheduleSource -> remoteDataSource.getByGroup(source.id)
            is TeacherScheduleSource -> remoteDataSource.getByTeacher(source.id)
            else -> Result0.Failure(ScheduleException.ScheduleNotFound)
        }.onSuccess {
            scheduleDao.setScheduleVersion(ScheduleVersionDb(source.idGlobal, ZonedDateTime.now()))
            localDataSource.set(it, source.idGlobal)
        }
        schedule
    }

    override suspend fun getSchedulePackListLocal(): Result0<SchedulePackList> = withContext(ioDispatcher) {
        val lessonTitles = HashSet<String>(2000)
        val lessonTypes = HashSet<String>(30)
        val lessonTeachers = HashSet<String>(1000)
        val lessonGroups = HashSet<String>(500)
        val lessonAuditoriums = HashSet<String>(600)

        val scheduleRes = localDataSource.get(ScheduleSource.PREFIX_ADVANCED_SEARCH)
        if (scheduleRes is Result0.Failure) return@withContext scheduleRes
        scheduleRes.onSuccess { schedule ->
            schedule.dailySchedules.forEach { dailySchedule ->
                dailySchedule.forEach { lessonPlace ->
                    lessonPlace.lessons.forEach { lesson ->
                        lessonTitles.add(lesson.title)
                        lessonTypes.add(lesson.type)
                        lesson.teachers.forEach { lessonTeachers.add(it.name) }
                        lesson.groups.forEach { lessonGroups.add(it.title) }
                        lesson.auditoriums.forEach { lessonAuditoriums.add(it.title) }
                    }
                }
            }
        }

        return@withContext Result0.Success(SchedulePackList(
            lessonTitles.sorted(),
            lessonTypes.sorted(),
            lessonTeachers.sorted(),
            lessonGroups.sorted(),
            lessonAuditoriums.sorted(),
        ))
    }

    override suspend fun getSchedulePackList(onProgressChanged: (Float) -> Unit): SchedulePackList =
        withContext(Dispatchers.Default) {
            val lessonTitles = HashSet<String>(2000)
            val lessonTypes = HashSet<String>(30)
            val lessonTeachers = HashSet<String>(1000)
            val lessonGroups = HashSet<String>(500)
            val lessonAuditoriums = HashSet<String>(600)

            val schedule = remoteDataSource
                .getAll(
                    lessonTitles,
                    lessonTypes,
                    lessonTeachers,
                    lessonGroups,
                    lessonAuditoriums,
                    onProgressChanged
                ).getOrNull()
            val downloadDateTime = ZonedDateTime.now()

            scheduleDao.setScheduleVersion(
                ScheduleVersionDb(
                    ScheduleSource.PREFIX_ADVANCED_SEARCH,
                    downloadDateTime
                )
            )
            localDataSource.set(schedule, ScheduleSource.PREFIX_ADVANCED_SEARCH)
            SchedulePackList(
                lessonTitles.sorted(),
                lessonTypes.sorted(),
                lessonTeachers.sorted(),
                lessonGroups.sorted(),
                lessonAuditoriums.sorted(),
            )
        }
}