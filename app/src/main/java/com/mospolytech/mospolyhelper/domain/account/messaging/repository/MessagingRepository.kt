package com.mospolytech.mospolyhelper.domain.account.messaging.repository

import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    suspend fun getDialog(dialogKey: String): Flow<Result2<List<Message>>>
    suspend fun getLocalDialog(dialogKey: String): Flow<Result2<List<Message>>>
    suspend fun sendMessage(dialogKey: String, message: String, fileNames: List<String>): Flow<Result2<List<Message>>>
    suspend fun deleteMessage(dialogKey: String, removeKey: String): Flow<Result2<List<Message>>>
    fun getName(): String
    fun getAvatar(): String
}