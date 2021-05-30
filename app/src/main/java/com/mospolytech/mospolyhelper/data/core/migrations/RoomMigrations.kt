package com.mospolytech.mospolyhelper.data.core.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `ScheduleVersionDb` " +
                "(`userScheduleId` TEXT NOT NULL, `downloadingDateTime` INTEGER NOT NULL, PRIMARY KEY(`userScheduleId`))")
    }
}