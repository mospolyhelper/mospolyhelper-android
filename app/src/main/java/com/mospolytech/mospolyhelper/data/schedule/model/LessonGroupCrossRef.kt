package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity

@Entity(primaryKeys = ["lessonId", "groupTitle"])
class LessonGroupCrossRef(
    val lessonId: Int,
    val groupTitle: String
)