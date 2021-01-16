package com.mospolytech.mospolyhelper.data.account.students.repository

import androidx.paging.PagedList
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.data.account.students.api.StudentsHerokuClient
import com.mospolytech.mospolyhelper.data.account.students.remote.StudentsRemoteDataSource
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.Executors


class StudentsRepositoryImpl(
    private val dataSource: StudentsRemoteDataSource,
    private val client: StudentsHerokuClient
) : StudentsRepository {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun getInfo(query: String): Flow<PagingData<Student>> {
        return Pager(
            PagingConfig(pageSize = 100, enablePlaceholders = false)
        ) {
            StudentsRemoteDataSource(client, query)
        }.flow
            .flowOn(ioDispatcher)

    }

    override fun invalidate() {
        dataSource.retry.invoke()
    }


}