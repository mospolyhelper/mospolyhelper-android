package com.mospolytech.mospolyhelper.data.account.auth.remote


import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.data.account.auth.api.AuthJwtHerokuClient
import com.mospolytech.mospolyhelper.domain.account.auth.model.JwtModel
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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