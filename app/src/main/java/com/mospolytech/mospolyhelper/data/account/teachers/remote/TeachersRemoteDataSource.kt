package com.mospolytech.mospolyhelper.data.account.teachers.remote

import androidx.paging.PagingSource
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.teachers.api.TeachersHerokuClient
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.model.TeachersSearchResult
import com.mospolytech.mospolyhelper.utils.Result
import java.lang.Exception

class TeachersRemoteDataSource(
    private val client: TeachersHerokuClient, private val sessionId: String, private val query: String): PagingSource<Int, Teacher>() {

    var retry:() -> Unit = {this.invalidate()}

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Teacher> {
        return try {
            val response = client.getTeachers(query, params.key?: 1, sessionId)
            val teachers = Klaxon().parse<TeachersSearchResult>(response)!!
            LoadResult.Page(
                teachers.teachers,
                null,
                if (teachers.currentPage < teachers.pageCount) teachers.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}