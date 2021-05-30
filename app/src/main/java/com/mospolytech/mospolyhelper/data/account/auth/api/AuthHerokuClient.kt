package com.mospolytech.mospolyhelper.data.account.auth.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.collections.mutableMapOf
import kotlin.collections.set

class AuthHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_AUTH = "$BASE_URL$ACCOUNT_MODULE/auth"
    }

    suspend fun auth(login: String, password: String, sessionId: String): String {
        val params = mutableMapOf(
            "login" to login,
            "password" to password
        )
        if (sessionId.isNotEmpty()) {
            params["sessionId"] = sessionId
        }
        return client.post(GET_AUTH) {
            contentType(ContentType.Application.Json)
            body = params
        }
    }
}