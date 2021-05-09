package com.mospolytech.mospolyhelper.data.account.dialogs.remote

import com.mospolytech.mospolyhelper.data.account.dialogs.api.DialogsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogsModel
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class DialogsRemoteDataSource(private val client: DialogsHerokuClient) {

    suspend fun getDialogs(sessionId: String): Result<DialogsModel> {
        return try {
            val res = client.getDialogs(sessionId)
            Result.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}