package com.mospolytech.mospolyhelper.data.account.marks.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class MarksLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(marks: String): Result<Marks> {
        return try {
            Result.success(Klaxon().parse<Marks>(marks)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(marks: Marks) {
        val currentInfo = Klaxon().toJsonString(marks)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Marks, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Marks, "")
    }
}