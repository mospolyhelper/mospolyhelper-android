package com.mospolytech.mospolyhelper.domain.account.teachers.usecase

import androidx.paging.PagingData
import com.mospolytech.mospolyhelper.domain.account.teachers.model.Teacher
import com.mospolytech.mospolyhelper.domain.account.teachers.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow

class TeachersUseCase(
    private val repository: TeachersRepository
) {

    fun getInfo(query: String): Flow<PagingData<Teacher>> =
        repository.getInfo(query)


}