package com.mospolytech.mospolyhelper.data.account.statements.remote

import com.mospolytech.mospolyhelper.data.account.statements.api.StatementsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StatementsRemoteDataSource(
    private val client: StatementsHerokuClient
) {
    suspend fun get(sessionId: String, semester: String?): Result2<Statements> {
        return try {
            val res = client.getMarks(sessionId, semester)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}