package com.mospolytech.mospolyhelper.domain.account.messaging.repository

import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    suspend fun getDialog(dialogKey: String): Flow<Result<List<Message>>>
    suspend fun getLocalDialog(dialogKey: String): Flow<Result<List<Message>>>
    suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result<Message>>
}