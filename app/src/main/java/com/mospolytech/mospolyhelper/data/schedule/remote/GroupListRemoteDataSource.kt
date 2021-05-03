package com.mospolytech.mospolyhelper.data.schedule.remote

import android.util.Log
import com.mospolytech.mospolyhelper.data.schedule.api.GroupListClient
import com.mospolytech.mospolyhelper.data.schedule.converter.GroupListRemoteConverter
import com.mospolytech.mospolyhelper.utils.TAG

class GroupListRemoteDataSource(
    private val client: GroupListClient,
    private val groupListConverter: GroupListRemoteConverter = GroupListRemoteConverter()
) {

    suspend fun get(): List<String>? {
        return try {
            val groupListString = client.getGroupList()
            return groupListConverter.parseGroupList(groupListString).sorted()
        } catch (e: Exception) {
            Log.e(TAG, "GroupList downloading and parsing exception", e)
            null
        }
    }
}