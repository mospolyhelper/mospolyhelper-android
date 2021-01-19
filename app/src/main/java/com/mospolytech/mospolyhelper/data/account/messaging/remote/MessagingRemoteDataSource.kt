package com.mospolytech.mospolyhelper.data.account.messaging.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.messaging.api.MessagingHerokuClient
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import com.mospolytech.mospolyhelper.utils.Result

class MessagingRemoteDataSource(
    private val client: MessagingHerokuClient,
) {
    suspend fun getMessages(sessionId: String, dialogKey: String): Result<List<Message>> {
        return try {
            val res = client.getMessages(sessionId, dialogKey)
            Result.success(Klaxon().parseArray(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(sessionId: String, dialogKey: String, message: String, fileNames: List<String>): Result<Message> {
        return try {
            val json = Klaxon().toJsonString(MessageSend(dialogKey, message, fileNames))
            val res = client.sendMessage(sessionId, MessageSend(dialogKey, message, fileNames))
            Result.success(Klaxon().parse(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}