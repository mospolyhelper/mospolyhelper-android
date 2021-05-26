package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity

@Entity(primaryKeys = ["lessonId", "auditoriumId"])
class LessonAuditoriumCrossRef(
    val lessonId: Int,
    val auditoriumId: Int
)