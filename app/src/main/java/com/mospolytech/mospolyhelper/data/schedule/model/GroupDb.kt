package com.mospolytech.mospolyhelper.data.schedule.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GroupDb(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "group_id")
    val id: Int,
    val title: String,
    val isEvening: Boolean
)