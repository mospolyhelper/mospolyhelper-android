package com.mospolytech.mospolyhelper.data.account.messaging.remote

import com.mospolytech.mospolyhelper.data.account.messaging.api.MessagingHerokuClient
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MessagingRemoteDataSource(
    private val client: MessagingHerokuClient,
) {
    suspend fun getMessages(sessionId: String, dialogKey: String): Result0<List<Message>> {
        return try {
            val res = client.getMessages(sessionId, dialogKey)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }

    suspend fun sendMessage(sessionId: String, dialogKey: String, message: String, fileNames: List<String>): Result0<List<Message>> {
        return try {
            val res = client.sendMessage(sessionId, MessageSend(dialogKey, message, fileNames))
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }

    suspend fun deleteMessage(sessionId: String, removeKey: String): Result0<List<Message>> {
        return try {
            val res = client.deleteMessage(sessionId, removeKey)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}