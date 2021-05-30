package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.GroupInfoApi
import com.mospolytech.mospolyhelper.domain.schedule.model.info.GroupsInfo
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GroupInfoRemoteDataSource(
    private val client: GroupInfoApi
) {
    suspend fun get(): Result2<GroupsInfo> {
        return try {
            val res = client.get()
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}