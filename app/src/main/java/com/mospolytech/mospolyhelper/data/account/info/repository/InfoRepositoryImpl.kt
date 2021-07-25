package com.mospolytech.mospolyhelper.data.account.info.repository

import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.account.info.remote.InfoRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InfoRepositoryImpl(
    private val dataSource: InfoRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : InfoRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getInfo() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = dataSource.get(sessionId)
        res.onSuccess { info ->
            prefDataSource.setObject(info)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalInfo() = flow {
        prefDataSource.getObject<Info>()?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)

}