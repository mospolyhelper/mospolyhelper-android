package com.mospolytech.mospolyhelper.data.account.marks.api

import io.ktor.client.*
import io.ktor.client.request.*

class MarksHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_MARKS = "$BASE_URL$ACCOUNT_MODULE/marks"
    }

    suspend fun getMarks(sessionId: String): String {
        return client.get(GET_MARKS) {
            header("sessionId", sessionId)
        }
    }
}