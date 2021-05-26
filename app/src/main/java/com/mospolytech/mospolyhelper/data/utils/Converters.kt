package com.mospolytech.mospolyhelper.data.utils

import androidx.room.TypeConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime

class Converters {
    @TypeConverter
    fun stringToSchedule(json: String): Schedule? {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun scheduleToString(schedule: Schedule?): String {
        return Json.encodeToString(schedule)
    }

    @TypeConverter
    fun stringToZonedDateTime(milliseconds: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC)
    }

    @TypeConverter
    fun zonedDateTimeToString(dateTime: ZonedDateTime): Long {
        return dateTime.toInstant().toEpochMilli()
    }

    @TypeConverter
    fun stringToLocalDate(days: Long): LocalDate {
        return LocalDate.ofEpochDay(days)
    }

    @TypeConverter
    fun localDateToString(date: LocalDate): Long {
        return date.toEpochDay()
    }
}