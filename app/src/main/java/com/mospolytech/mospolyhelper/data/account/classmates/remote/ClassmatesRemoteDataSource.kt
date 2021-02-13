package com.mospolytech.mospolyhelper.data.account.classmates.remote

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.account.classmates.api.ClassmatesHerokuClient
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class ClassmatesRemoteDataSource(
    private val client: ClassmatesHerokuClient
) {
    suspend fun get(sessionId: String): Result<List<Classmate>> {
        return try {
            val res = client.getInfo(sessionId)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}