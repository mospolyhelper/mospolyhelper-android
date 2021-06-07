package com.mospolytech.mospolyhelper.data.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mospolytech.mospolyhelper.data.deadline.DeadlineDAO
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleDao
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleVersionDb
import com.mospolytech.mospolyhelper.data.utils.Converters
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline

@Database(
    version = 2,
    entities = [
        Deadline::class,
        ScheduleVersionDb::class
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDeadlinesDAO() : DeadlineDAO

    abstract fun getScheduleDao(): ScheduleDao
}