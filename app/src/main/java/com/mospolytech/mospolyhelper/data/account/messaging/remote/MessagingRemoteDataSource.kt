package com.mospolytech.mospolyhelper.data.account.messaging.remote

import com.mospolytech.mospolyhelper.data.account.messaging.api.MessagingHerokuClient
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MessagingRemoteDataSource(
    private val client: MessagingHerokuClient,
) {
    suspend fun getMessages(sessionId: String, dialogKey: String): Result2<List<Message>> {
        return try {
            val res = client.getMessages(sessionId, dialogKey)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    suspend fun sendMessage(sessionId: String, dialogKey: String, message: String, fileNames: List<String>): Result2<List<Message>> {
        return try {
            val res = client.sendMessage(sessionId, MessageSend(dialogKey, message, fileNames))
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    suspend fun deleteMessage(sessionId: String, removeKey: String): Result<List<Message>> {
        return try {
            val res = client.deleteMessage(sessionId, removeKey)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}