package com.mospolytech.mospolyhelper.data.account.statements.local

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StatementsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(statements: String): Result2<Statements> {
        return try {
            Result2.success(Json.decodeFromString<Statements>(statements))
        } catch (e: Exception) {
            Result2.failure(e)
        }
    }

    fun set(statements: Statements) {
        val currentInfo = Json.encodeToString(statements)
        if (getJson() != currentInfo)
            prefDataSource.set(PreferenceKeys.Statements, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.get(PreferenceKeys.Statements, "")
    }
}