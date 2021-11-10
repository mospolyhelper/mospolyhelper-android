package com.mospolytech.mospolyhelper.data.account.repository

import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.getResultObject
import com.mospolytech.mospolyhelper.data.utils.setObject
import com.mospolytech.mospolyhelper.domain.account.model.dialog.Message
import com.mospolytech.mospolyhelper.domain.account.model.dialog.MessageSend
import com.mospolytech.mospolyhelper.domain.account.repository.MessagingRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MessagingRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
): MessagingRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getDialog(dialogKey: String, emitLocal: Boolean): Flow<Result0<List<Message>>> = flow {
        emit(Result0.Loading)
        if (emitLocal) {
            prefDataSource.getResultObject<List<Message>>(dialogKey)?.let {
                emit(it)
                emit(Result0.Loading)
            }
        }
        val res = api.getMessages(dialogKey)
            .onSuccess {
                prefDataSource.setObject(it, dialogKey)
            }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result0<List<Message>>> = flow {
        emit(Result0.Loading)
        val messageModel = MessageSend(message = message, fileNames = fileNames, dialogKey = dialogKey)
        val res = api.sendMessage(messageModel)
            .onSuccess {
                prefDataSource.setObject(it, dialogKey)
            }
        emit(res)
    }.flowOn(ioDispatcher)

    override suspend fun deleteMessage(dialogKey: String, removeKey: String): Flow<Result0<List<Message>>>  = flow {
        emit(Result0.Loading)
        val res = api.deleteMessage(removeKey)
            .onSuccess {
                prefDataSource.setObject(it, dialogKey)
            }
        emit(res)
    }.flowOn(ioDispatcher)


}