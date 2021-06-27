package com.mospolytech.mospolyhelper.data.account.applications.repository

import com.mospolytech.mospolyhelper.data.account.applications.remote.ApplicationsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApplicationsRepositoryImpl(
    private val dataSource: ApplicationsRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : ApplicationsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getApplications() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it, PreferenceKeys.Applications)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<List<Application>>(PreferenceKeys.Applications)?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)




}