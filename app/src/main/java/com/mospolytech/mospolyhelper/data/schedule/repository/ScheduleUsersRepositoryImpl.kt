package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.setAsJson
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
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

    private val savedUsersChangesFlow = MutableSharedFlow<List<ScheduleSource>>(extraBufferCapacity = 64)
    private val currentUserChangesFlow = MutableSharedFlow<ScheduleSource?>(extraBufferCapacity = 64)

    override fun getSavedUsers() = flow {
        val users = prefDataSource.getFromJson<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        emit(users)
        emitAll(savedUsersChangesFlow)
    }.flowOn(ioDispatcher)

    override suspend fun setSavedUsers(savedSources: List<ScheduleSource>) = withContext(ioDispatcher) {
        prefDataSource.setAsJson(PreferenceKeys.ScheduleSavedIds, savedSources)
        savedUsersChangesFlow.emit(savedSources)
    }

    override suspend fun addSavedUser(source: ScheduleSource) = withContext(ioDispatcher) {
        val users = prefDataSource.getFromJson<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        if (source !in users) {
            setSavedUsers((users + source).sorted())
        }
    }

    override suspend fun removeSavedUser(source: ScheduleSource) = withContext(ioDispatcher) {
        val users = prefDataSource.getFromJson<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        setSavedUsers(users - source)
    }

    override fun getScheduleUsers() = flow {
        emit((groupsDataSource.get() ?: emptyList()).map { StudentScheduleSource(it, it) } +
                (teachersDataSource.get() ?: emptyMap()).map { TeacherScheduleSource(it.key, it.value) }
                    .sortedBy { it.title + it.id }.distinct())
    }

    override fun getCurrentUser() = flow {
        emit(prefDataSource.getFromJson(PreferenceKeys.ScheduleUser))
        emitAll(currentUserChangesFlow)
    }

    override suspend fun setCurrentUser(source: ScheduleSource?) = withContext(ioDispatcher) {
        prefDataSource.setAsJson(PreferenceKeys.ScheduleUser, source)
        currentUserChangesFlow.emit(source)
    }
}