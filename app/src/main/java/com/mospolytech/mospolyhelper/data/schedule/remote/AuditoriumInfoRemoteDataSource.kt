package com.mospolytech.mospolyhelper.data.schedule.remote

import com.mospolytech.mospolyhelper.data.schedule.api.AuditoriumInfoApi
import com.mospolytech.mospolyhelper.domain.schedule.model.info.AuditoriumsInfo
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AuditoriumInfoRemoteDataSource(
    private val client: AuditoriumInfoApi
) {
    suspend fun get(): Result2<AuditoriumsInfo> {
        return try {
            val res = client.get()
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}