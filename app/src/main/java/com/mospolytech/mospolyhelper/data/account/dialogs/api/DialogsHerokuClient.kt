package com.mospolytech.mospolyhelper.data.account.dialogs.api

import io.ktor.client.*
import io.ktor.client.request.*

class DialogsHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_DIALOGS = "$BASE_URL$ACCOUNT_MODULE/dialogs"
    }

    suspend fun getDialogs(sessionId: String): String {
        return client.get(GET_DIALOGS) {
            header("sessionId", sessionId)
        }
    }

}