package com.mospolytech.mospolyhelper.data.account.dialogs.repository

import com.mospolytech.mospolyhelper.data.account.dialogs.remote.DialogsRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DialogsRepositoryImpl(private val remoteDataSource: DialogsRemoteDataSource,
                            private val prefDataSource: SharedPreferencesDataSource
                            ): DialogsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDialogs() = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = remoteDataSource.getDialogs(sessionId)
        res.onSuccess {
            prefDataSource.setObject(it, PreferenceKeys.Dialogs)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalDialogs() = flow {
        prefDataSource.getObject<List<DialogModel>>(PreferenceKeys.Dialogs)?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)

}