package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.GroupListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.local.TeacherListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.TeacherListRepository

class TeacherListRepositoryImpl(
    private val remoteDataSource: TeacherListRemoteDataSource,
    private val localDataSource: TeacherListLocalDataSource
) : TeacherListRepository {

    override suspend fun getTeacherList(): Map<String, String> {
        return remoteDataSource.get() ?: localDataSource.get()?.associate { Pair(it, it) } ?: emptyMap()
    }
}