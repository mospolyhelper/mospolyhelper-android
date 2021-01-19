package com.mospolytech.mospolyhelper.data.account.info.remote

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class InfoRemoteDataSource(
    private val client: InfoHerokuClient
) {
    suspend fun get(sessionId: String): Result<Info> {
        return try {
            val res = client.getInfo(sessionId)
            Result.success(Klaxon().parse(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}