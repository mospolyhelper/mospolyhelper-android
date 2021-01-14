package com.mospolytech.mospolyhelper.data.account.applications.api

import io.ktor.client.*
import io.ktor.client.request.*

class ApplicationsHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_APPLICATIONS = "$BASE_URL$ACCOUNT_MODULE/applications"
    }

    suspend fun getApplications(sessionId: String): String {
        return client.get(GET_APPLICATIONS) {
            header("sessionId", sessionId)
        }
    }
}