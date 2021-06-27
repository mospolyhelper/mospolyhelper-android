package com.mospolytech.mospolyhelper.data.account.classmates.remote

import com.mospolytech.mospolyhelper.data.account.classmates.api.ClassmatesHerokuClient
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ClassmatesRemoteDataSource(
    private val client: ClassmatesHerokuClient
) {
    suspend fun get(sessionId: String): Result0<List<Classmate>> {
        return try {
            val res = client.getInfo(sessionId)
            Result0.Success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result0.Failure(e)
        }
    }
}