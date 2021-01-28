package com.mospolytech.mospolyhelper.data.account.statements.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.marks.api.MarksHerokuClient
import com.mospolytech.mospolyhelper.data.account.statements.api.StatementsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.Result

class StatementsRemoteDataSource(
    private val client: StatementsHerokuClient
) {
    suspend fun get(sessionId: String, semester: String?): Result<Statements> {
        return try {
            val res = client.getMarks(sessionId, semester)
            Result.success(Klaxon().parse(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}