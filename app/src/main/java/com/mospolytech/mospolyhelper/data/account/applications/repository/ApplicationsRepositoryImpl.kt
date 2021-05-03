package com.mospolytech.mospolyhelper.data.account.applications.repository

import com.mospolytech.mospolyhelper.data.account.applications.local.ApplicationsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.applications.remote.ApplicationsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApplicationsRepositoryImpl(
    private val dataSource: ApplicationsRemoteDataSource,
    private val localDataSource: ApplicationsLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : ApplicationsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Suppress("UNCHECKED_CAST")
    override suspend fun getApplications() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        if (res.isSuccess) localDataSource.set(res.value as List<Application>)
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo(): Flow<Result<List<Application>>>{
        val info = localDataSource.getJson()
        return flow {
                if (info.isNotEmpty()) emit(localDataSource.get(info))
            }.flowOn(ioDispatcher)

    }



}