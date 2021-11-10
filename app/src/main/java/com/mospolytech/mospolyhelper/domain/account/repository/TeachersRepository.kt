package com.mospolytech.mospolyhelper.domain.account.repository

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.model.teachers.Teacher
import kotlinx.coroutines.flow.Flow

interface TeachersRepository {
    fun getTeachers(query: String): Flow<PagingData<Teacher>>
}