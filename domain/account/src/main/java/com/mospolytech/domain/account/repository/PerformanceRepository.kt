package com.mospolytech.domain.account.repository

import com.mospolytech.domain.account.model.Marks

interface PerformanceRepository {
    fun getCourses(): List<Int>
    fun getSemesters(): List<Int>
    fun getMarksByCourse(course: Int): List<Marks>
    fun getMarksBySemester(semester: Int): Marks
    fun getMarks(): List<Marks>
}