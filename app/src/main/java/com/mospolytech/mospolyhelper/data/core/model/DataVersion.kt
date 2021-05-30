package com.mospolytech.mospolyhelper.data.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
class DataVersion(
    @PrimaryKey
    val key: String,
    val version: ZonedDateTime
)