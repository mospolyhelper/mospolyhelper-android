package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LessonWithFeaturesDb(
    @Embedded val lesson: LessonDb,
    @Relation(
        parentColumn = "lesson_id",
        entityColumn = "teacher_id",
        associateBy = Junction(LessonTeacherCrossRef::class)
    )
    val teachers: List<TeacherDb>,
    @Relation(
        parentColumn = "lesson_id",
        entityColumn = "group_id",
        associateBy = Junction(LessonGroupCrossRef::class)
    )
    val groups: List<GroupDb>,
    @Relation(
        parentColumn = "lesson_id",
        entityColumn = "auditorium_id",
        associateBy = Junction(LessonAuditoriumCrossRef::class)
    )
    val auditoriums: List<AuditoriumDb>
)
