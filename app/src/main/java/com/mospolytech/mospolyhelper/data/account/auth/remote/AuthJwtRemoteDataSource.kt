package com.mospolytech.mospolyhelper.data.account.auth.remote


import com.mospolytech.mospolyhelper.data.account.auth.api.AuthJwtHerokuClient
import com.mospolytech.mospolyhelper.domain.account.auth.model.JwtModel
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthJwtRemoteDataSource(
    private val client: AuthJwtHerokuClient
) {

    suspend fun authJwt(login: String, password: String): Result2<JwtModel> {
        return try {
            val res = client.auth(login, password)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    suspend fun refresh(accessToken: String, refreshToken: String): Result2<String> {
        return try {
            val res = client.refresh(accessToken, refreshToken)
            Result2.success(res)
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}