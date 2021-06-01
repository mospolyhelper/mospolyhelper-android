package com.mospolytech.mospolyhelper.data.account.messaging.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MessagingLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun getDialog(dialog: String): Result2<List<Message>> {
        return try {
            Result2.success(Json.decodeFromString(dialog))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    fun setDialog(dialog: List<Message>, dialogKey: String) {
        val currentInfo = Json.encodeToString(dialog)
        if (getJson(dialogKey) != currentInfo)
            prefDataSource.set(dialogKey, currentInfo)
    }

    fun getJson(dialogKey: String): String {
        return prefDataSource.get(dialogKey, "")
    }

}