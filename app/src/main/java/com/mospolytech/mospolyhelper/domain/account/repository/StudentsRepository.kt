package com.mospolytech.mospolyhelper.domain.account.repository

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.model.students.Student
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun getInfo(query: String): Flow<PagingData<Student>>
}