package com.mospolytech.mospolyhelper.data.account.group_marks.remote

import com.mospolytech.mospolyhelper.data.account.group_marks.api.GroupMarksHerokuClient
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheet
import com.mospolytech.mospolyhelper.domain.account.group_marks.model.GradeSheetMark
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GroupMarksRemoteDataSource(private val client: GroupMarksHerokuClient) {

    suspend fun getGradeSheet(sessionId: String, guid: String): Result0<GradeSheet> {
        return try {
            val res = client.getGradeSheet(sessionId, guid)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }

    suspend fun getGradeSheetMarks(sessionId: String, guid: String): Result0<List<GradeSheetMark>> {
        return try {
            val res = client.getGradeSheetMarks(sessionId, guid)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}