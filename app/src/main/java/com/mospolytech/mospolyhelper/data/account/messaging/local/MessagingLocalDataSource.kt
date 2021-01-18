package com.mospolytech.mospolyhelper.data.account.messaging.local

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.messaging.model.Message
import com.mospolytech.mospolyhelper.utils.Result

class MessagingLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun getDialog(dialog: String): Result<List<Message>> {
        return try {
            Result.success(Klaxon().parseArray(dialog)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun setDialog(dialog: List<Message>, dialogKey: String) {
        val currentInfo = Klaxon().toJsonString(dialog)
        if (getJson(dialogKey) != currentInfo)
            prefDataSource.setString(dialogKey, currentInfo)
    }

    fun getJson(dialogKey: String): String {
        return prefDataSource.getString(dialogKey, "")
    }

}