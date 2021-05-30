package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleDao
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleRepository
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.utils.Result2
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ScheduleRepositoryImpl(
    private val remoteDataSource: ScheduleRemoteDataSource,
    private val localDataSource: ScheduleLocalDataSource,
    private val scheduleDao: ScheduleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ScheduleRepository {
    private val changesFlow = MutableSharedFlow<Schedule?>(extraBufferCapacity = 64)

    override fun getSchedule(
        user: UserSchedule?
    ) = flow {
        if (user == null) {
            emit(null)
        } else {
            val scheduleVersion = scheduleDao.getScheduleVersion(user)
            when {
                user is AdvancedSearchSchedule -> emit(localDataSource.get(user)?.filter(user.filters))
                scheduleVersion == null ||
                        scheduleVersion.downloadingDateTime
                            .until(ZonedDateTime.now(), ChronoUnit.DAYS) >= 1 -> {
                    emit(refresh(user).getOrNull())
                }
                else -> {
                    emit(localDataSource.get(user))
                }
            }
        }
        emitAll(changesFlow)
    }.flowOn(ioDispatcher)

    override suspend fun getScheduleVersion(user: UserSchedule): ScheduleVersionDb? {
        return scheduleDao.getScheduleVersion(user)
    }

    override suspend fun updateSchedule(user: UserSchedule?) = withContext(ioDispatcher) {
        if (user != null && user !is AdvancedSearchSchedule) {
            changesFlow.emit(refresh(user).getOrNull())
        }
    }

    private suspend fun refresh(user: UserSchedule): Result2<Schedule> = coroutineScope {
        val schedule = when (user) {
            is StudentSchedule -> remoteDataSource.getByGroup(user.id)
            is TeacherSchedule -> remoteDataSource.getByTeacher(user.id)
            else -> Result2.failure(Exception())
        }.onSuccess {
            this.launch {
                scheduleDao.setScheduleVersion(ScheduleVersionDb(user.idGlobal, ZonedDateTime.now()))
                localDataSource.set(it, user.idGlobal)
            }
        }
        schedule
    }

    override suspend fun getAnySchedules(onProgressChanged: (Float) -> Unit): SchedulePackList =
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
                    UserSchedule.PREFIX_ADVANCED_SEARCH,
                    downloadDateTime
                )
            )
            localDataSource.set(schedule, UserSchedule.PREFIX_ADVANCED_SEARCH)
            SchedulePackList(
                lessonTitles.sorted(),
                lessonTypes.sorted(),
                lessonTeachers.sorted(),
                lessonGroups.sorted(),
                lessonAuditoriums.sorted(),
            )
        }
}