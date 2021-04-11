package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.TeacherInfoApi
import com.mospolytech.mospolyhelper.domain.schedule.model.info.TeachersInfo
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TeacherInfoRemoteDataSource(
    private val client: TeacherInfoApi
) {
    suspend fun get(): Result<TeachersInfo> {
        return try {
            val res = client.get()
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}