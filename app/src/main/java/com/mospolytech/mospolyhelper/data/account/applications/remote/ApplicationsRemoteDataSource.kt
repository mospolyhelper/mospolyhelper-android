package com.mospolytech.mospolyhelper.data.account.applications.remote

import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ApplicationsRemoteDataSource(
    private val client: ApplicationsHerokuClient
) {
    suspend fun get(sessionId: String): Result2<List<Application>> {
        return try {
            val res = client.getApplications(sessionId)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}