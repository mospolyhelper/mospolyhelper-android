package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AuditoriumDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "auditorium_id")
    val id: Int,
    val title: String,
    val type: String,
    val url: String,
    val color: String
)