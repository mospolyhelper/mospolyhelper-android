package com.mospolytech.mospolyhelper.data.account.classmates.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class ClassmatesLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(classmates: String): Result<List<Classmate>> {
        return try {
            Result.success(Klaxon().parseArray(classmates)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(classmates: List<Classmate>) {
        val currentInfo = Klaxon().toJsonString(classmates)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Classmates, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Classmates, "")
    }
}