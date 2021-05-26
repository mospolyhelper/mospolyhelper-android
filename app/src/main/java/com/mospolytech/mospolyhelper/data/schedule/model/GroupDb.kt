package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroupDb(
    @PrimaryKey
    val groupTitle: String,
    val isEvening: Boolean
)