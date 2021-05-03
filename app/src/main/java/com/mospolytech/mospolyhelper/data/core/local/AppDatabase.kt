package com.mospolytech.mospolyhelper.data.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mospolytech.mospolyhelper.data.deadline.DeadlineDAO
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline

@Database(entities = [Deadline::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDeadlinesDAO() : DeadlineDAO
}