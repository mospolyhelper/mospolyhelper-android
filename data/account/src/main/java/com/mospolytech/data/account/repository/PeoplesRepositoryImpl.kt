package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.model.Teacher
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.domain.base.model.PagingDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PeoplesRepositoryImpl(
    private val api: AccountService
): PeoplesRepository {
    override fun getTeachers(
        name: String,
        page: Int,
        pageSize: Int
    ) = api.getTeachers(name, page, pageSize)
            .flowOn(Dispatchers.IO)

    override fun getStudents(
        name: String,
        page: Int,
        pageSize: Int
    ) = api.getStudents(name, page, pageSize)
            .flowOn(Dispatchers.IO)

    override fun getClassmates() =
        api.getClassmates()
            .flowOn(Dispatchers.IO)

}