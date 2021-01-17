package com.mospolytech.mospolyhelper.data.account.classmates.api

import io.ktor.client.*
import io.ktor.client.request.*

class ClassmatesHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_INFO = "$BASE_URL$ACCOUNT_MODULE/classmates"
    }

    suspend fun getInfo(sessionId: String): String {
        return client.get(GET_INFO) {
            header("sessionId", sessionId)
        }
    }
}