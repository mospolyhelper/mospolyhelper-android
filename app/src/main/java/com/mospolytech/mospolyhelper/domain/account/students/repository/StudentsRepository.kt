package com.mospolytech.mospolyhelper.domain.account.students.repository

import androidx.paging.PagedList
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow

interface StudentsRepository {
    fun getInfo(query: String): Flow<PagingData<Student>>
}