package com.mospolytech.mospolyhelper.data.account.auth.remote

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.utils.Result

class AuthRemoteDataSource(
    private val client: AuthHerokuClient
) {
    suspend fun auth(login: String, password: String, sessionId: String): Result<String> {
        return try {
            val res = client.auth(login, password, sessionId)
            Result.success(res.trim('"'))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}