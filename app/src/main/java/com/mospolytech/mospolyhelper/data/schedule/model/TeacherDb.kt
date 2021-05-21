package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TeacherDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "teacher_id")
    val id: Int,
    val name: String
)