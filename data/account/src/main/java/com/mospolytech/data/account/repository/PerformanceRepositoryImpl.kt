package com.mospolytech.data.account.repository

import com.mospolytech.data.account.api.AccountService
import com.mospolytech.domain.account.model.Marks
import com.mospolytech.domain.account.repository.PerformanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PerformanceRepositoryImpl(
    private val api: AccountService
): PerformanceRepository {
    override fun getCourses() =
        api.getCourses()
            .flowOn(Dispatchers.IO)

    override fun getSemesters() =
        api.getSemesters()
            .flowOn(Dispatchers.IO)

    override fun getMarksByCourse(course: Int) =
        api.getMarksByCourse(course.toString())
            .flowOn(Dispatchers.IO)

    override fun getMarksBySemester(semester: Int) =
        api.getMarksBySemester(semester.toString())
            .flowOn(Dispatchers.IO)

    override fun getMarks() =
        api.getMarks()
            .flowOn(Dispatchers.IO)

}