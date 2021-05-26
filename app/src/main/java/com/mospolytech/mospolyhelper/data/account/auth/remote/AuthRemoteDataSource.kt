package com.mospolytech.mospolyhelper.data.account.auth.remote


import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.utils.Result2

class AuthRemoteDataSource(
    private val client: AuthHerokuClient
) {
    suspend fun auth(login: String, password: String, sessionId: String): Result2<String> {
        return try {
            val res = client.auth(login, password, sessionId)
            Result2.success(res.trim('"'))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

}