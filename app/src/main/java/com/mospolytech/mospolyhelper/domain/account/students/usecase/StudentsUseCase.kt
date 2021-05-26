package com.mospolytech.mospolyhelper.domain.account.students.usecase

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.students.model.Student
import com.mospolytech.mospolyhelper.domain.account.students.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow

class StudentsUseCase(
    private val repository: StudentsRepository
) {

    fun getInfo(query: String): Flow<PagingData<Student>> =
        repository.getInfo(query)

}