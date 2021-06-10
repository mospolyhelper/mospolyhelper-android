package com.mospolytech.mospolyhelper

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.mospolytech.mospolyhelper.data.core.local.AppDatabase
import com.mospolytech.mospolyhelper.data.core.migrations.MIGRATION_1_2
import org.junit.Rule
import org.junit.Test
import java.io.IOException


class DatabaseMigrationTest {
    companion object {
        private const val TEST_DB = "migration-test"
        private val allMigrations = arrayOf(
            MIGRATION_1_2
        )
    }

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun check_migrations() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        ).addMigrations(*allMigrations)
            .build()
            .apply {
                openHelper.writableDatabase
                close()
            }
    }
}