package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.GroupListClient
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter

class GroupListRemoteDataSource(
    private val client: GroupListClient = GroupListClient(),
    private val groupListConverter: GroupListRemoteConverter = GroupListRemoteConverter()
) {

    suspend fun get(): List<String> {
        val groupListString = client.getGroupList()
        return groupListConverter.parseGroupList(groupListString).sorted()
    }
}