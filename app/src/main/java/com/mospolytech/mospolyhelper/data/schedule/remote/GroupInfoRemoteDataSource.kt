package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.GroupInfoApi
import com.mospolytech.mospolyhelper.domain.schedule.model.info.GroupsInfo
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GroupInfoRemoteDataSource(
    private val client: GroupInfoApi
) {
    suspend fun get(): Result<GroupsInfo> {
        return try {
            val res = client.get()
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}