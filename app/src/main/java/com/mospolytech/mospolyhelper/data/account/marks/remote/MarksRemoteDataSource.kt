package com.mospolytech.mospolyhelper.data.account.marks.remote

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.Result

class MarksRemoteDataSource(
    private val client: MarksHerokuClient
) {
    suspend fun get(sessionId: String): Result<Marks> {
        return try {
            val res = client.getMarks(sessionId)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}