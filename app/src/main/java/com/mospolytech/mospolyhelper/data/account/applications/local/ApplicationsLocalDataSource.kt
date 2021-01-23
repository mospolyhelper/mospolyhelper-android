package com.mospolytech.mospolyhelper.data.account.applications.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class ApplicationsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(applications: String): Result<List<Application>> {
        return try {
            Result.success(Klaxon().parseArray(applications)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(applications: List<Application>) {
        val currentInfo = Klaxon().toJsonString(applications)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Applications, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Applications, "")
    }
}