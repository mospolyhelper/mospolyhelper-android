package com.mospolytech.mospolyhelper.data.account.students.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.utils.Result

class StudentsRemoteDataSource(
    private val client: StudentsHerokuClient
) {
    suspend fun get(searchQuery: String, page: Int): Result<StudentsSearchResult> {
        return try {
            val res = client.getStudents(searchQuery, page)
            Result.success(Klaxon().parse(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}