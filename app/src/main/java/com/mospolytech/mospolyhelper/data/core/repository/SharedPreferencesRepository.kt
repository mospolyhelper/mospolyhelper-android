package com.mospolytech.mospolyhelper.data.core.repository

import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.core.repository.PreferencesRepository

class SharedPreferencesRepository(
    private val dataSource: SharedPreferencesDataSource
) : PreferencesRepository {
    override val dataLastUpdatedFlow = dataSource.dataLastUpdatedObservable

    override fun get(key: String, defaultValue: Boolean): Boolean {
        return dataSource.get(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Int): Int {
        return dataSource.get(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Long): Long {
        return dataSource.get(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Float): Float {
        return dataSource.get(key, defaultValue)
    }

    override fun get(key: String, defaultValue: String): String {
        return dataSource.get(key, defaultValue)
    }

    override fun get(key: String, defaultValue: Set<String>): Set<String> {
        return dataSource.get(key, defaultValue)
    }


    override fun set(key: String, value: Boolean) {
        dataSource.set(key, value)
    }

    override fun set(key: String, value: Int) {
        dataSource.set(key, value)
    }

    override fun set(key: String, value: Long) {
        dataSource.set(key, value)
    }

    override fun set(key: String, value: Float) {
        dataSource.set(key, value)
    }

    override fun set(key: String, value: String) {
        dataSource.set(key, value)
    }

    override fun set(key: String, value: Set<String>) {
        dataSource.set(key, value)
    }
}