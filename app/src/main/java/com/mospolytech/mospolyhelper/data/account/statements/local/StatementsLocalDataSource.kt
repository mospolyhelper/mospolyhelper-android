package com.mospolytech.mospolyhelper.data.account.statements.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class StatementsLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {
    fun get(statements: String): Result<Statements> {
        return try {
            Result.success(Klaxon().parse<Statements>(statements)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(statements: Statements) {
        val currentInfo = Klaxon().toJsonString(statements)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Statements, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Statements, "")
    }
}