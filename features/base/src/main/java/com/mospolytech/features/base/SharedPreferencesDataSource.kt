package com.mospolytech.features.base

import android.content.SharedPreferences
import com.mospolytech.data.base.PreferencesDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SharedPreferencesDataSource(
    private val prefs: SharedPreferences
) : PreferencesDataSource,  SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope  {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
    private val dataLastUpdatedFlow = MutableSharedFlow<String?>(replay = 1)
    override val preferencesUpdates: Flow<String?> = dataLastUpdatedFlow

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        launch {
            dataLastUpdatedFlow.emit(key)
        }
    }

    override fun get(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    override fun get(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue)!!
    }

    override fun get(key: String, defaultValue: Set<String>): Set<String> {
        return prefs.getStringSet(key, defaultValue)!!
    }


    override fun set(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun set(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override fun set(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    override fun set(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    override fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun set(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}