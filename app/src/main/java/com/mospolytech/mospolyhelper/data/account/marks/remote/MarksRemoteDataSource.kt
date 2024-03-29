package com.mospolytech.mospolyhelper.data.account.marks.remote

import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MarksRemoteDataSource(
    private val client: MarksHerokuClient
) {
    suspend fun get(sessionId: String): Result0<Marks> {
        return try {
            val res = client.getMarks(sessionId)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}