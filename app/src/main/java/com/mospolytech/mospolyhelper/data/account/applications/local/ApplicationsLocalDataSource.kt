package com.mospolytech.mospolyhelper.data.account.applications.local

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class ApplicationsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(applications: String): Result<List<Application>> {
        return try {
            Result.success(Json.decodeFromString(applications))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(applications: List<Application>) {
        val currentInfo = Json.encodeToString(applications)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Applications, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Applications, "")
    }
}