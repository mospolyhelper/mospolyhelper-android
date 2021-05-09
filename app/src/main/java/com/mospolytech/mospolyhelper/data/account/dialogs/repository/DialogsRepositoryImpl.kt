package com.mospolytech.mospolyhelper.data.account.dialogs.repository

import com.mospolytech.mospolyhelper.data.account.dialogs.local.DialogsLocalDataSource
import com.mospolytech.mospolyhelper.data.account.dialogs.remote.DialogsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogsModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DialogsRepositoryImpl(private val localDataSource: DialogsLocalDataSource,
                            private val remoteDataSource: DialogsRemoteDataSource,
                            private val prefDataSource: SharedPreferencesDataSource
                            ): DialogsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDialogs(): Flow<Result<DialogsModel>> = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = remoteDataSource.getDialogs(sessionId)
        res.onSuccess {
            localDataSource.set(it)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalDialogs(): Flow<Result<DialogsModel>> = flow {
        emit(localDataSource.get())
    }.flowOn(ioDispatcher)

}