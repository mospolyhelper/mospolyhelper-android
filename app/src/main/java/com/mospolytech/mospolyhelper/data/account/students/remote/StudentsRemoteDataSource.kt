package com.mospolytech.mospolyhelper.data.account.students.remote

import android.util.Log
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class StudentsRemoteDataSource(
    private val client: StudentsHerokuClient, private val query: String): PagingSource<Int, Student>() {

    var retry:() -> Unit = {this.invalidate()}

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            val response = client.getStudents(query, params.key?: 1)
            val students = Klaxon().parse<StudentsSearchResult>(response)!!
            LoadResult.Page(
                students.portfolios,
                null,
                if (students.currentPage < students.pageCount) students.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }


}