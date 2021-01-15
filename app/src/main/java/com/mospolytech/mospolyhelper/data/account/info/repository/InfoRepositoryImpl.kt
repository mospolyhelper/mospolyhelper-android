package com.mospolytech.mospolyhelper.data.account.info.repository

import com.mospolytech.mospolyhelper.data.account.info.local.InfoLocalDataSource
import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InfoRepositoryImpl(
    private val dataSource: InfoRemoteDataSource,
    private val localDataSource: InfoLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : InfoRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.getString(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        localDataSource.set(res.value as Info)
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo(): Flow<Result<Info>>{
        val info = localDataSource.getJson()
        return flow {
                if (info != "") emit(localDataSource.get(info))
            }.flowOn(ioDispatcher)

    }



}