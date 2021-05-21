package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["lesson_id", "group_id"])
class LessonGroupCrossRef(
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    @ColumnInfo(name = "group_id")
    val groupId: Int
)