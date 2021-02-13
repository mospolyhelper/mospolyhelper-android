package com.mospolytech.mospolyhelper.data.account.classmates.local

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result

class ClassmatesLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(classmates: String): Result<List<Classmate>> {
        return try {
            Result.success(Json.decodeFromString(classmates))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(classmates: List<Classmate>) {
        val currentInfo = Json.encodeToString(classmates)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Classmates, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Classmates, "")
    }
}