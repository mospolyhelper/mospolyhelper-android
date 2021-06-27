package com.mospolytech.mospolyhelper.data.account.teachers.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.model.TeachersSearchResult2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TeachersRemoteDataSource(
    private val client: TeachersHerokuClient,
    private val sessionId: String,
    private val query: String
    ): PagingSource<Int, Teacher>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Teacher> {
        return try {
            val response = client.getTeachers(query, params.key?: 1, sessionId)
            val teachers = Json.decodeFromString<TeachersSearchResult2>(response)
            LoadResult.Page(
                teachers.teachers,
                if (teachers.currentPage <= 1) null else teachers.currentPage - 1,
                if (teachers.currentPage < teachers.pageCount) teachers.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Teacher>): Int? = null
}