package com.mospolytech.mospolyhelper.domain.account.students.repository

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun getInfo(query: String): Flow<PagingData<Student>>
}