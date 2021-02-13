package com.mospolytech.mospolyhelper.data.account.statements.api

import io.ktor.client.*
import io.ktor.client.request.*

class StatementsHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_MARKS = "$BASE_URL$ACCOUNT_MODULE/grade-sheets"
    }

    suspend fun getMarks(sessionId: String, semester: String?): String {
        return client.get(GET_MARKS) {
            parameter("semester", semester)
            header("sessionId", sessionId)
        }
    }
}