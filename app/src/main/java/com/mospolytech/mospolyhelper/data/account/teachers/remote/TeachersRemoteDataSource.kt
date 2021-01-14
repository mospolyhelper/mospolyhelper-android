package com.mospolytech.mospolyhelper.data.account.teachers.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.domain.account.teachers.model.TeachersSearchResult
import com.mospolytech.mospolyhelper.utils.Result

class TeachersRemoteDataSource(
    private val client: TeachersHerokuClient
) {
    suspend fun get(searchQuery: String, page: Int, sessionId: String): Result<TeachersSearchResult> {
        return try {
            val res = client.getTeachers(searchQuery, page, sessionId)
            Result.success(Klaxon().parse(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}