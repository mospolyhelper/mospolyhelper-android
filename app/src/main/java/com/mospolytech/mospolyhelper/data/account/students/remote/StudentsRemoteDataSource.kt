package com.mospolytech.mospolyhelper.data.account.students.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StudentsRemoteDataSource(
    private val client: StudentsHerokuClient,
    private val query: String
    ): PagingSource<Int, Student>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            val response = client.getStudents(query, params.key?: 1)
            val students = Json.decodeFromString<StudentsSearchResult2>(response)
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