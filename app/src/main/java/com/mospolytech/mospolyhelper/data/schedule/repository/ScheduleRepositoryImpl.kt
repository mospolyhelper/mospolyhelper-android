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
        user: UserSchedule?
    ) = flow<Result0<Schedule>> {
        if (user == null) {
            emit(Result0.Failure(IllegalArgumentException()))
        } else {
            if (user is AdvancedSearchSchedule) {
                emit(localDataSource.get(user).map { it.filter(user.filters) })
            } else {
                val version = scheduleDao.getScheduleVersion(user)
                if (version == null ||
                    version.downloadingDateTime
                        .until(ZonedDateTime.now(), ChronoUnit.DAYS) >= 1) {
                    emit(refresh(user))
                } else {
                    emit(localDataSource.get(user))
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun getScheduleVersion(user: UserSchedule): ScheduleVersionDb? {
        return scheduleDao.getScheduleVersion(user)
    }

    override suspend fun updateSchedule(user: UserSchedule?) = withContext(ioDispatcher) {
        if (user != null && user !is AdvancedSearchSchedule) {
            refresh(user)
            dataLastUpdatedFlow.emit(ZonedDateTime.now())
        }
    }

    private suspend fun refresh(user: UserSchedule): Result0<Schedule> = coroutineScope {
        val schedule = when (user) {
            is StudentSchedule -> remoteDataSource.getByGroup(user.id)
            is TeacherSchedule -> remoteDataSource.getByTeacher(user.id)
            else -> Result0.Failure(Exception())
        }.onSuccess {
            scheduleDao.setScheduleVersion(ScheduleVersionDb(user.idGlobal, ZonedDateTime.now()))
            localDataSource.set(it, user.idGlobal)
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