package com.mospolytech.mospolyhelper.repository.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room

import androidx.room.RoomDatabase
import com.mospolytech.mospolyhelper.repository.database.dao.DeadlineDAO
import com.mospolytech.mospolyhelper.repository.database.entity.Deadline

@Database(entities = [Deadline::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDeadlinesDAO() : DeadlineDAO
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "database")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}