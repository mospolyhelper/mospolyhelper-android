package com.mospolytech.mospolyhelper.data.account.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.utils.AccountPagingDataSource
import com.mospolytech.mospolyhelper.domain.account.model.students.Student
import com.mospolytech.mospolyhelper.domain.account.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow


class StudentsRepositoryImpl(private val api: AccountApi) : StudentsRepository {

    override fun getInfo(query: String): Flow<PagingData<Student>> {
        return Pager(
            PagingConfig(pageSize = 100, enablePlaceholders = false)
        ) {
            AccountPagingDataSource { page->
                api.getStudents(query, page)
            }
        }.flow
    }


}