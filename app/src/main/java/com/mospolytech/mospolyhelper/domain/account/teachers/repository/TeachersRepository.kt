package com.mospolytech.mospolyhelper.domain.account.teachers.repository

import androidx.paging.PagedList
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface TeachersRepository {
    fun getInfo(query: String): Flow<PagingData<Teacher>>
}