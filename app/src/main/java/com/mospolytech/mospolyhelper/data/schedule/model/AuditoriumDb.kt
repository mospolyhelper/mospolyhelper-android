package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AuditoriumDb(
    @PrimaryKey(autoGenerate = true)
    val auditoriumId: Int,
    val title: String,
    val type: String,
    val url: String,
    val color: String
)