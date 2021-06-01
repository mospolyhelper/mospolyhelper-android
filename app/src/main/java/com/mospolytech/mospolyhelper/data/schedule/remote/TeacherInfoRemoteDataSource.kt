package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.TeacherInfoApi
import com.mospolytech.mospolyhelper.domain.schedule.model.info.TeachersInfo
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TeacherInfoRemoteDataSource(
    private val client: TeacherInfoApi
) {
    suspend fun get(): Result0<TeachersInfo> {
        return try {
            val res = client.get()
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}