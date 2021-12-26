package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.model.Teacher
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.domain.base.model.PagingDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PeoplesRepositoryImpl(private val api: AccountService): PeoplesRepository {
    override fun getTeachers(
        name: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<PagingDTO<Teacher>>> = flow {
        emit(api.getTeachers(name, page, pageSize).toResult())
    }

    override fun getStudents(
        name: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<PagingDTO<Student>>> = flow {
        emit(api.getStudents(name, page, pageSize).toResult())
    }

    override fun getClassmates(): Flow<Result<List<Student>>> = flow {
        emit(api.getClassmates().toResult())
    }

}