package com.mospolytech.data.base

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val preferencesUpdates: Flow<String?>
    fun get(key: String, defaultValue: Boolean): Boolean
    fun get(key: String, defaultValue: Int): Int
    fun get(key: String, defaultValue: Long): Long
    fun get(key: String, defaultValue: Float): Float
    fun get(key: String, defaultValue: String): String
    fun get(key: String, defaultValue: Set<String>): Set<String>
    fun set(key: String, value: Boolean)
    fun set(key: String, value: Int)
    fun set(key: String, value: Long)
    fun set(key: String, value: Float)
    fun set(key: String, value: String)
    fun set(key: String, value: Set<String>)
    fun clear(): Unit
}