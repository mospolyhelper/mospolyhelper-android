package com.mospolytech.mospolyhelper.data.account.dialogs.remote

import com.mospolytech.mospolyhelper.data.account.dialogs.api.DialogsHerokuClient
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DialogsRemoteDataSource(private val client: DialogsHerokuClient) {

    suspend fun getDialogs(sessionId: String): Result2<List<DialogModel>> {
        return try {
            val res = client.getDialogs(sessionId)
            Result2.success(Json.decodeFromString(res))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }
}