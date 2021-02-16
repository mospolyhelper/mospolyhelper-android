package com.mospolytech.mospolyhelper.data.account.auth.remote


import com.mospolytech.mospolyhelper.data.account.auth.api.AuthHerokuClient
import com.mospolytech.mospolyhelper.data.account.auth.api.AuthJwtHerokuClient
import com.mospolytech.mospolyhelper.domain.account.auth.model.JwtModel
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthJwtRemoteDataSource(
    private val client: AuthJwtHerokuClient
) {

    suspend fun authJwt(login: String, password: String): Result<JwtModel> {
        return try {
            val res = client.auth(login, password)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refresh(accessToken: String, refreshToken: String): Result<String> {
        return try {
            val res = client.refresh(accessToken, refreshToken)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}