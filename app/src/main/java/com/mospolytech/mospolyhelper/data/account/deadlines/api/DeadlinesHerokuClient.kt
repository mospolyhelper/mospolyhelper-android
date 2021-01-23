package com.mospolytech.mospolyhelper.data.account.deadlines.api

import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.MyPortfolio
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class DeadlinesHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_DEADLINES = "$BASE_URL$ACCOUNT_MODULE/myportfolio"
    }

    suspend fun getDeadlines(sessionId: String): String {
        return client.get(GET_DEADLINES) {
            header("sessionId", sessionId)
        }
    }

    suspend fun setDeadlines(sessionId: String, myPortfolio: MyPortfolio): String {
        return client.post(GET_DEADLINES) {
            contentType(ContentType.Application.Json)
            header("sessionId", sessionId)
            body = myPortfolio
        }
    }
}