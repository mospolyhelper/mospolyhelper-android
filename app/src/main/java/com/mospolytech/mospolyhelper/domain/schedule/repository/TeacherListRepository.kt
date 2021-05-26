package com.mospolytech.mospolyhelper.domain.schedule.repository

interface TeacherListRepository {
    suspend fun getTeacherList(): Map<String, String>
}