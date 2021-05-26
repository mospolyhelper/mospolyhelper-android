package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity

@Entity(primaryKeys = ["lessonId", "teacherName"])
class LessonTeacherCrossRef(
    val lessonId: Int,
    val teacherName: String
)