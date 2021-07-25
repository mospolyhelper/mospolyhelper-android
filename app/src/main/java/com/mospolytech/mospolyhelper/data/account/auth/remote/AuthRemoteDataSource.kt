package com.mospolytech.mospolyhelper.data.account.auth.remote


import com.mospolytech.mospolyhelper.data.account.auth.api.AuthJwtHerokuClient
import com.mospolytech.mospolyhelper.domain.account.auth.model.JwtModel
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuthRemoteDataSource(
    private val client: AuthJwtHerokuClient
) {

    suspend fun authJwt(login: String, password: String): Result0<JwtModel> {
        return try {
            val res = client.auth(login, password)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }

    suspend fun refresh(accessToken: String, refreshToken: String): Result0<String> {
        return try {
            val res = client.refresh(accessToken, refreshToken)
            Result0.Success(res)
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}