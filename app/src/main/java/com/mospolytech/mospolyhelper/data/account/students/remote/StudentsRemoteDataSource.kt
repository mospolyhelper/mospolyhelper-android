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
    private val client: StudentsHerokuClient): PagingSource<Int, Student>() {

    var query = ""

    var state = flow<Result<List<Student>>> { }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            state = flow{ emit(Result.loading()) }
            val response = client.getStudents(query, params.key?: 0)
            val students = Klaxon().parse<StudentsSearchResult>(response)!!
            state = flow { emit(Result.success(students.portfolios))}
            LoadResult.Page(
                students.portfolios,
                null,
                if (students.currentPage < students.pageCount) students.currentPage + 1 else null
            )
        } catch (exception: Exception) {
            state = flow { emit(Result.failure(exception))}
            return LoadResult.Error(exception)
        }
    }


}