package com.mospolytech.mospolyhelper.data.core.local

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DataVersionDataSource(
    private val dataSource: SharedPreferencesDataSource
) {
    companion object {
        private const val PREFIX = "__VERSION"
    }
    fun set(key: String, date: ZonedDateTime) {
        dataSource.set(PREFIX + key, date.toInstant().toEpochMilli())
    }

    fun get(key: String): ZonedDateTime {
        val milliseconds = dataSource.get(PREFIX + key, Long.MIN_VALUE)
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC)
    }
}