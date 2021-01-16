package com.mospolytech.mospolyhelper.data.account.info.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class InfoLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(info: String): Result<Info> {
        return try {
                Result.success(Klaxon().parse<Info>(info)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(info: Info) {
        val currentInfo = Klaxon().toJsonString(info)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Info, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Info, "")
    }
}