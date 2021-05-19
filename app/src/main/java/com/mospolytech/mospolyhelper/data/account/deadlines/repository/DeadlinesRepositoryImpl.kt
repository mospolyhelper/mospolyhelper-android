package com.mospolytech.mospolyhelper.data.account.deadlines.repository

import com.mospolytech.mospolyhelper.data.account.deadlines.local.DeadlinesLocalDataSource
import com.mospolytech.mospolyhelper.data.account.deadlines.remote.DeadlinesRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeadlinesRepositoryImpl(
    private val dataSource: DeadlinesRemoteDataSource,
    private val localDataSource: DeadlinesLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : DeadlinesRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Suppress("UNCHECKED_CAST")
    override suspend fun getDeadlines() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            localDataSource.set(it)
        }
        emit(res)
    }.flowOn(ioDispatcher)


    override suspend fun getLocalInfo(): Flow<Result<List<Deadline>>>{
        val info = localDataSource.getJson()
        return flow {
                if (info.isNotEmpty()) emit(localDataSource.get(info))
            }.flowOn(ioDispatcher)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun setDeadlines(deadlines: List<Deadline>) = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.set(sessionId, deadlines)
        res.onSuccess {
            localDataSource.set(it)
        }
        emit(res)
    }.flowOn(ioDispatcher)


}