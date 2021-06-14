package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleUsersRepository
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ScheduleUsersRepositoryImpl(
    private val groupsDataSource: GroupListRemoteDataSource,
    private val teachersDataSource: TeacherListRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScheduleUsersRepository {

    private val savedUsersChangesFlow = MutableSharedFlow<List<UserSchedule>>(extraBufferCapacity = 64)
    private val currentUserChangesFlow = MutableSharedFlow<UserSchedule?>(extraBufferCapacity = 64)

    override fun getSavedUsers() = flow {
        val users = prefDataSource.getFromJson<List<UserSchedule>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        emit(users)
        emitAll(savedUsersChangesFlow)
    }.flowOn(ioDispatcher)

    override suspend fun setSavedUsers(savedUsers: List<UserSchedule>) = withContext(ioDispatcher) {
        prefDataSource.setAsJson(PreferenceKeys.ScheduleSavedIds, savedUsers)
        savedUsersChangesFlow.emit(savedUsers)
    }

    override suspend fun addSavedUser(user: UserSchedule) = withContext(ioDispatcher) {
        val users = prefDataSource.getFromJson<List<UserSchedule>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        if (user !in users) {
            setSavedUsers((users + user).sorted())
        }
    }

    override suspend fun removeSavedUser(user: UserSchedule) = withContext(ioDispatcher) {
        val users = prefDataSource.getFromJson<List<UserSchedule>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        setSavedUsers(users - user)
    }

    override fun getScheduleUsers() = flow {
        emit((groupsDataSource.get() ?: emptyList()).map { StudentSchedule(it, it) } +
                (teachersDataSource.get() ?: emptyMap()).map { TeacherSchedule(it.key, it.value) }
                    .sortedBy { it.title + it.id }.distinct())
    }

    override fun getCurrentUser() = flow {
        emit(prefDataSource.getFromJson(PreferenceKeys.ScheduleUser))
        emitAll(currentUserChangesFlow)
    }

    override suspend fun setCurrentUser(user: UserSchedule?) = withContext(ioDispatcher) {
        prefDataSource.setAsJson(PreferenceKeys.ScheduleUser, user)
        currentUserChangesFlow.emit(user)
    }
}