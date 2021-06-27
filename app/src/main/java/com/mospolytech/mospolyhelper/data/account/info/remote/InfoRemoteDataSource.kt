package com.mospolytech.mospolyhelper.data.account.info.remote

import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class InfoRemoteDataSource(
    private val client: InfoHerokuClient
) {
    suspend fun get(sessionId: String): Result0<Info> {
        return try {
            val res = client.getInfo(sessionId)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}