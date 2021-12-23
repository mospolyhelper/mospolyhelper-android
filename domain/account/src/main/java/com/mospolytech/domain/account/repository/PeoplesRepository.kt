package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.model.Teacher
import com.mospolytech.domain.base.model.PagingDTO
import kotlinx.coroutines.flow.Flow

interface PeoplesRepository {
    fun getTeachers(name: String = "", page: Int = 1, pageSize: Int = 100): Flow<Result<PagingDTO<Teacher>>>
    fun getStudents(name: String = "", page: Int = 1, pageSize: Int = 100): Flow<Result<PagingDTO<Student>>>
    fun getClassmates(name: String = ""): Flow<Result<List<Student>>>
}