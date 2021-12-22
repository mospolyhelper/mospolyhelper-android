package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.data.base.retrofit.toResult
import com.mospolytech.domain.account.model.Marks
import com.mospolytech.domain.account.repository.PerformanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PerformanceRepositoryImpl(private val api: AccountService): PerformanceRepository {
    override fun getCourses(): Flow<Result<List<Int>>> = flow {
        emit(api.getCourses().toResult())
    }

    override fun getSemesters(): Flow<Result<List<Int>>> = flow {
        emit(api.getSemesters().toResult())
    }

    override fun getMarksByCourse(course: Int): Flow<Result<List<Marks>>> = flow {
        emit(api.getMarksByCourse(course.toString()).toResult())
    }

    override fun getMarksBySemester(semester: Int): Flow<Result<Marks>> = flow {
        emit(api.getMarksBySemester(semester.toString()).toResult())
    }

    override fun getMarks(): Flow<Result<List<Marks>>> = flow {
        emit(api.getMarks().toResult())
    }

}