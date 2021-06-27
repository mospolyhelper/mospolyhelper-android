package com.mospolytech.mospolyhelper.data.account.applications.remote

import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ApplicationsRemoteDataSource(
    private val client: ApplicationsHerokuClient
) {
    suspend fun get(sessionId: String): Result0<List<Application>> {
        return try {
            val res = client.getApplications(sessionId)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}