package com.mospolytech.mospolyhelper.data.account.auth.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AuthJwtHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com/v0.2"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_AUTH = "$BASE_URL$ACCOUNT_MODULE/authenticate"
        private const val GET_REFRESH = "$BASE_URL$ACCOUNT_MODULE/refresh"
    }

    suspend fun auth(login: String, password: String): String {
        val params = mutableMapOf(
            "login" to login,
            "password" to password
        )
        return client.post(GET_AUTH) {
            contentType(ContentType.Application.Json)
            body = params
        }
    }

    suspend fun refresh(accessToken: String, refreshToken: String): String {
        return client.post(GET_REFRESH) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $accessToken")
            body = refreshToken
        }
    }
}