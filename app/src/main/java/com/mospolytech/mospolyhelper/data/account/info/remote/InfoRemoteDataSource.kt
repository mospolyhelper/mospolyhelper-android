package com.mospolytech.mospolyhelper.data.account.info.remote

import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class InfoRemoteDataSource(
    private val client: InfoHerokuClient
) {
    suspend fun get(sessionId: String): Result2<Info> {
        return try {
            val res = client.getInfo(sessionId)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}