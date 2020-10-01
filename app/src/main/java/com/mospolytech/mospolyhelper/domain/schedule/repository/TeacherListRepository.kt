package com.mospolytech.mospolyhelper.domain.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.TeacherListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource

interface TeacherListRepository {
    suspend fun getTeacherList(): Map<String, String>
}