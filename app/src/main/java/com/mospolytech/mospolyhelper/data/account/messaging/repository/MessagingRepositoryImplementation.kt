package com.mospolytech.mospolyhelper.data.account.messaging.repository

import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.account.messaging.remote.MessagingRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.data.utils.getObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.repository.MessagingRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MessagingRepositoryImplementation(
    private val remoteDataSource: MessagingRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
): MessagingRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDialog(dialogKey: String): Flow<Result0<List<Message>>> = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = remoteDataSource.getMessages(sessionId, dialogKey)
        res.onSuccess {
            prefDataSource.setObject(it, dialogKey)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun getLocalDialog(dialogKey: String) = flow {
        prefDataSource.getObject<List<Message>>(dialogKey)?.let {
            emit(it)
        }
    }.flowOn(ioDispatcher)

    override suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result0<List<Message>>> = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = remoteDataSource.sendMessage(sessionId, dialogKey, message, fileNames)
        res.onSuccess {
            prefDataSource.setObject(it, dialogKey)
        }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun deleteMessage(dialogKey: String, removeKey: String): Flow<Result0<List<Message>>>  = flow {
        val sessionId = prefDataSource.get(
            PreferenceKeys.SessionId,
            PreferenceDefaults.SessionId
        )
        val res = remoteDataSource.deleteMessage(sessionId, removeKey)
        res.onSuccess {
            prefDataSource.setObject(it, dialogKey)
        }
        emit(res)
    }.flowOn(ioDispatcher)


}