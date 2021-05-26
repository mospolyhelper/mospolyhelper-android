package com.mospolytech.mospolyhelper.data.account.auth.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*

class AuthClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://e.mospolytech.ru"
        private const val AUTH = "$BASE_URL/?p=login"
    }

    suspend fun auth(login: String, password: String, sessionId: String): String {
        return client.post(AUTH) {
            contentType(ContentType.Application.FormUrlEncoded.withCharset(Charset.forName("Windows-1251")))
            body = listOf(
                "ulogin" to login,
                "upassword" to password,
                "auth_action" to "userlogin"
            ).formUrlEncode()
        }
    }
}