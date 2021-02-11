package com.mospolytech.mospolyhelper.domain.account.students.usecase

import androidx.paging.PagedList
import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.repository.InfoRepository
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.marks.repository.MarksRepository
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.model.StudentsSearchResult
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class StudentsUseCase(
    private val repository: StudentsRepository
) {

    fun getInfo(query: String): Flow<PagingData<Student>> =
        repository.getInfo(query)

}