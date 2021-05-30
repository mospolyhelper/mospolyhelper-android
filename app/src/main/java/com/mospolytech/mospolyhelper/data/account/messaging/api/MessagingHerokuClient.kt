package com.mospolytech.mospolyhelper.data.account.messaging.api

import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.domain.account.messaging.model.MessageSend
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class MessagingHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_DIALOGS = "$BASE_URL$ACCOUNT_MODULE/dialog"
        private const val SEND_MESSAGE = "$BASE_URL$ACCOUNT_MODULE/message"
    }

    suspend fun getMessages(sessionId: String, dialogKey: String): String {
        return client.get(GET_DIALOGS) {
            header("sessionId", sessionId)
            parameter("dialogKey", dialogKey)
        }
    }

    suspend fun sendMessage(sessionId: String, message: MessageSend): String {
        return client.post(SEND_MESSAGE) {
            header("sessionId", sessionId)
            contentType(ContentType.Application.Json)
            body = message
        }
    }

    suspend fun deleteMessage(sessionId: String, removeKey: String): String {
        return client.delete(SEND_MESSAGE) {
            header("sessionId", sessionId)
            parameter("removeKey", removeKey)
        }
    }
}