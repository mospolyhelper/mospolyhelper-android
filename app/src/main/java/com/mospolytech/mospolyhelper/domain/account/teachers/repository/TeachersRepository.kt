package com.mospolytech.mospolyhelper.domain.account.teachers.repository

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import kotlinx.coroutines.flow.Flow

interface TeachersRepository {
    fun getInfo(query: String): Flow<PagingData<Teacher>>
}