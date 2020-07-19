package com.mospolytech.mospolyhelper.repository.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.deadline.DeadlineDAO
import com.mospolytech.mospolyhelper.repository.deadline.Deadline

@Database(entities = [Deadline::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDeadlinesDAO() : DeadlineDAO
}