package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TeacherDb(
    @PrimaryKey
    val teacherName: String
)