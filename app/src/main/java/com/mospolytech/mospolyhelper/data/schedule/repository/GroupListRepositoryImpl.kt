package com.mospolytech.mospolyhelper.data.schedule.repository

import com.mospolytech.mospolyhelper.data.schedule.local.GroupListLocalDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.GroupListRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.repository.GroupListRepository

class GroupListRepositoryImpl(
    private val localDataSource: GroupListLocalDataSource,
    private val remoteDataSource: GroupListRemoteDataSource
) : GroupListRepository {

    override suspend fun getGroupList(): List<String> {
        return remoteDataSource.get() ?: localDataSource.get() ?: emptyList()
    }
}