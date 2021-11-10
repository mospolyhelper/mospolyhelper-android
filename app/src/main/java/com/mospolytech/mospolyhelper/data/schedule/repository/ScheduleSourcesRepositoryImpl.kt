package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.TeacherScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.ScheduleSourcesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ScheduleSourcesRepositoryImpl(
    private val groupsDataSource: GroupListRemoteDataSource,
    private val teachersDataSource: TeacherListRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ScheduleSourcesRepository {

    private val savedUsersChangesFlow = MutableSharedFlow<List<ScheduleSource>>(extraBufferCapacity = 64)
    private val currentUserChangesFlow = MutableSharedFlow<ScheduleSource?>(extraBufferCapacity = 64)

    override fun getFavoriteScheduleSources() = flow {
        val users = prefDataSource.getObject<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        emit(users)
        emitAll(savedUsersChangesFlow)
    }.flowOn(ioDispatcher)

    private suspend fun setFavoriteScheduleSources(savedSources: List<ScheduleSource>) = withContext(ioDispatcher) {
        prefDataSource.setObject(PreferenceKeys.ScheduleSavedIds, savedSources)
        savedUsersChangesFlow.emit(savedSources)
    }

    override suspend fun addFavoriteScheduleSource(source: ScheduleSource) = withContext(ioDispatcher) {
        val users = prefDataSource.getObject<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        if (source !in users) {
            setFavoriteScheduleSources((users + source).sorted())
        }
    }

    override suspend fun removeFavoriteScheduleSource(source: ScheduleSource) = withContext(ioDispatcher) {
        val users = prefDataSource.getObject<List<ScheduleSource>>(PreferenceKeys.ScheduleSavedIds)
            ?: emptyList()
        setFavoriteScheduleSources(users - source)
    }

    override fun getScheduleSources() = flow {
        emit((groupsDataSource.get() ?: emptyList()).map { StudentScheduleSource(it, it) } +
                (teachersDataSource.get() ?: emptyMap()).map { TeacherScheduleSource(it.key, it.value) }
                    .sortedBy { it.title + it.id }.distinct())
    }

    override fun getSelectedScheduleSource() = flow {
        emit(prefDataSource.getObject(PreferenceKeys.ScheduleUser))
        emitAll(currentUserChangesFlow)
    }

    override suspend fun setSelectedScheduleSource(source: ScheduleSource?) = withContext(ioDispatcher) {
        prefDataSource.setObject(PreferenceKeys.ScheduleUser, source)
        currentUserChangesFlow.emit(source)
    }
}