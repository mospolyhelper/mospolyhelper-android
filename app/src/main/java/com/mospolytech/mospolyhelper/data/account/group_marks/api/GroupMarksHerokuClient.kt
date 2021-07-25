package com.mospolytech.mospolyhelper.data.account.group_marks.api

import io.ktor.client.*
import io.ktor.client.request.*

class GroupMarksHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_GRADE_SHEET = "$BASE_URL$ACCOUNT_MODULE/grade-sheet"
        private const val GET_GROUP_MARKS = "$BASE_URL$ACCOUNT_MODULE/grade-sheet-marks"
    }

    suspend fun getGradeSheet(sessionId: String, guid: String): String {
        return client.get(GET_GRADE_SHEET) {
            parameter("guid", guid)
            header("sessionId", sessionId)
        }
    }

    suspend fun getGradeSheetMarks(sessionId: String, guid: String): String {
        return client.get(GET_GROUP_MARKS) {
            parameter("guid", guid)
            header("sessionId", sessionId)
        }
    }
}