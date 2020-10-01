package com.mospolytech.mospolyhelper.data.schedule.local

import android.content.SharedPreferences
import android.util.Log
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.TAG
import java.lang.Exception

class SavedIdsLocalDataSource(
    private val prefDataSource: SharedPreferencesDataSource
) {
    fun get(): Set<Pair<Boolean, String>>? {
        return try {
            prefDataSource.getStringSet(PreferenceKeys.ScheduleSavedIds, emptySet())
                .map {
                    Pair(
                        it.last() == '1',
                        it.take(it.length - 1)
                    )
                }.toSet()
        } catch (e: Exception) {
            Log.e(TAG, "SavedIds local data source get exception", e)
            null
        }
    }

    fun set(pair: Set<Pair<Boolean, String>>) {
        try {
            prefDataSource.setStringSet(
                PreferenceKeys.ScheduleSavedIds,
                pair.map { it.second + if (it.first) '1' else '0' }.toSet()
            )
        } catch (e: Exception) {
            Log.e(TAG, "SavedIds local data source set exception", e)
        }
    }
}