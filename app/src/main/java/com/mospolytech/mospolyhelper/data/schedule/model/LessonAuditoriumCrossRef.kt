package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["lesson_id", "auditorium_id"])
class LessonAuditoriumCrossRef(
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    @ColumnInfo(name = "auditorium_id")
    val auditoriumId: Int
)