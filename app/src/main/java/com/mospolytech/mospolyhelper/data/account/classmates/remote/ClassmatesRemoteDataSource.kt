package com.mospolytech.mospolyhelper.data.account.classmates.remote

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.classmates.api.ClassmatesHerokuClient
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class ClassmatesRemoteDataSource(
    private val client: ClassmatesHerokuClient
) {
    suspend fun get(sessionId: String): Result<List<Classmate>> {
        return try {
            val res = client.getInfo(sessionId)
            Result.success(Klaxon().parseArray(res)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}