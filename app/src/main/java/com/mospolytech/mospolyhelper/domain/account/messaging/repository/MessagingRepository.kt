package com.mospolytech.mospolyhelper.domain.account.messaging.repository

import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    suspend fun getDialog(dialogKey: String): Flow<Result0<List<Message>>>
    suspend fun getLocalDialog(dialogKey: String): Flow<Result0<List<Message>>>
    suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result0<List<Message>>>
    suspend fun deleteMessage(dialogKey: String, removeKey: String): Flow<Result0<List<Message>>>
}