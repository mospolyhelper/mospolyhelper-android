package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class LessonWithFeaturesDb(
    @Embedded val lesson: LessonDb,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "teacherName",
        associateBy = Junction(LessonTeacherCrossRef::class)
    )
    val teachers: List<TeacherDb>,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "groupTitle",
        associateBy = Junction(LessonGroupCrossRef::class)
    )
    val groups: List<GroupDb>,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "auditoriumId",
        associateBy = Junction(LessonAuditoriumCrossRef::class)
    )
    val auditoriums: List<AuditoriumDb>
)
