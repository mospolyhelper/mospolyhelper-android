package com.mospolytech.mospolyhelper.data.schedule.local

import android.content.SharedPreferences
import android.util.Log
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.Exception

class SavedIdsLocalDataSource(
    private val prefDataSource: SharedPreferencesDataSource
) {
    fun get(): Set<UserSchedule>? {
        return try {
            prefDataSource.getStringSet(PreferenceKeys.ScheduleSavedIds, emptySet())
                .mapNotNull {
                    try {
                        Json.decodeFromString<UserSchedule>(it)
                    } catch (e: Exception) {
                        null
                    }
                }.toSet()
        } catch (e: Exception) {
            Log.e(TAG, "SavedIds local data source get exception", e)
            null
        }
    }

    fun set(pair: Set<UserSchedule>) {
        try {
            prefDataSource.setStringSet(
                PreferenceKeys.ScheduleSavedIds,
                pair.map { Json.encodeToString(it) }.toSet()
            )
        } catch (e: Exception) {
            Log.e(TAG, "SavedIds local data source set exception", e)
        }
    }
}