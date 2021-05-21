package com.mospolytech.mospolyhelper.data.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mospolytech.mospolyhelper.data.deadline.DeadlineDAO
import com.mospolytech.mospolyhelper.data.schedule.local.ScheduleDao
import com.mospolytech.mospolyhelper.data.schedule.model.*
import com.mospolytech.mospolyhelper.data.utils.Converters
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline

@Database(
    entities = [
        Deadline::class,
        LessonDb::class,
        TeacherDb::class,
        GroupDb::class,
        AuditoriumDb::class,
        LessonTeacherCrossRef::class,
        LessonGroupCrossRef::class,
        LessonAuditoriumCrossRef::class,
        ScheduleDb::class
               ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDeadlinesDAO() : DeadlineDAO

    abstract fun getScheduleDao(): ScheduleDao
}