package com.mospolytech.mospolyhelper.data.account.deadlines.local

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.data.account.info.api.InfoHerokuClient
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import java.io.StringReader

class DeadlinesLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun get(deadlines: String): Result<List<Deadline>> {
        return try {
            val res = Klaxon().parseArray<Deadline>(deadlines)!!
            res.sortedBy {
                it.pinned
            }
            res.sortedBy {
                !it.completed
            }
            Result.success(res)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun set(deadlines: List<Deadline>) {
        deadlines.sortedBy {
            it.pinned
        }
        deadlines.sortedBy {
            !it.completed
        }
        val currentInfo = Klaxon().toJsonString(deadlines)
        if (getJson() != currentInfo)
            prefDataSource.setString(PreferenceKeys.Deadlines, currentInfo)
    }

    fun getJson(): String {
        return prefDataSource.getString(PreferenceKeys.Deadlines, "")
    }
}