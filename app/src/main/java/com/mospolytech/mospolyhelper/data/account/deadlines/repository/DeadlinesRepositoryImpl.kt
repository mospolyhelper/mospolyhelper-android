package com.mospolytech.mospolyhelper.data.account.deadlines.repository

import com.mospolytech.mospolyhelper.data.account.deadlines.remote.DeadlinesRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeadlinesRepositoryImpl(
    private val dataSource: DeadlinesRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : DeadlinesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDeadlines() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it, PreferenceKeys.Deadlines)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<List<Deadline>>(PreferenceKeys.Deadlines)?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)

    override suspend fun setDeadlines(deadlines: List<Deadline>) = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.set(sessionId, deadlines)
        res.onSuccess {
            prefDataSource.setObject(it, PreferenceKeys.Deadlines)
        }
        emit(res)
    }.flowOn(ioDispatcher)


}