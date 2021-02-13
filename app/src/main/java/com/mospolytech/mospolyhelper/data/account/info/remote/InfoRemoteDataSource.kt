package com.mospolytech.mospolyhelper.data.account.info.remote

import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class InfoRemoteDataSource(
    private val client: InfoHerokuClient
) {
    suspend fun get(sessionId: String): Result<Info> {
        return try {
            val res = client.getInfo(sessionId)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}