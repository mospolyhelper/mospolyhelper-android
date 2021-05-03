package com.mospolytech.mospolyhelper.data.core.local

import android.content.SharedPreferences

class SharedPreferencesDataSource(
    private val prefs: SharedPreferences
) {
    fun get(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun get(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun get(key: String, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun get(key: String, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    fun get(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue)!!
    }

    fun get(key: String, defaultValue: Set<String>): Set<String> {
        return prefs.getStringSet(key, defaultValue)!!
    }


    fun set(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun set(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun set(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun set(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun set(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }
}