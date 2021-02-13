package com.mospolytech.mospolyhelper.data.account.students.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import java.lang.Exception

class StudentsRemoteDataSource(
    private val client: StudentsHerokuClient, private val query: String): PagingSource<Int, Student>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            val response = client.getStudents(query, params.key?: 1)
            val students = Json.decodeFromString<StudentsSearchResult>(response)
            LoadResult.Page(
                students.portfolios,
                if (students.currentPage <= 1) null else students.currentPage - 1,
                if (students.currentPage < students.pageCount) students.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Student>): Int? = null

}