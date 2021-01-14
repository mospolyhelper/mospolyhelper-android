package com.mospolytech.mospolyhelper.data.account.applications.remote

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.applications.api.ApplicationsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.*

class ApplicationsRemoteDataSource(
    private val client: ApplicationsHerokuClient
) {
    suspend fun get(sessionId: String): Result<List<Application>> {
        return try {
            val res = client.getApplications(sessionId)
            Result.success(Klaxon().parseArray(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}