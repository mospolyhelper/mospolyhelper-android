package com.mospolytech.mospolyhelper.data.schedule.local

import androidx.room.*
import com.mospolytech.mospolyhelper.data.schedule.model.LessonWithFeaturesDb
import com.mospolytech.mospolyhelper.data.schedule.model.ScheduleDb
import com.mospolytech.mospolyhelper.data.schedule.model.TeacherDb

@Dao
abstract class ScheduleDao {
    @Transaction
    @Query("SELECT * FROM LessonDb")
    abstract fun getAllLessons(): List<LessonWithFeaturesDb>

    @Query("SELECT * FROM ScheduleDb WHERE user = :user")
    abstract fun getScheduleByUser(user: String): ScheduleDb

    @Query("SELECT * FROM TeacherDb WHERE name = :name")
    abstract fun getTeacherByName(name: String): List<TeacherDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTeacher(teacher: TeacherDb)

    @Transaction
    open fun insertOrUpdateTeachers(teachers: List<TeacherDb>) {
        for (teacher in teachers) {
            val foundTeachers = getTeacherByName(teacher.name)
            if (foundTeachers.isEmpty()) {
                insertTeacher(teacher)
            }
        }
    }
}