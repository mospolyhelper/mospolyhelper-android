package com.mospolytech.mospolyhelper.data.schedule.local

import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleLocalDataSource(
    private val localConverter: ScheduleLocalConverter
) {

    companion object {
        const val CurrentExtension = "current"
        const val OldExtension = "backup"
        const val CustomExtension = "custom"
        const val SCHEDULE_FOLDER = "cached_schedules"
        const val SCHEDULE_SESSION_FOLDER = "session"
        const val SCHEDULE_REGULAR_FOLDER = "regular"
    }
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME


    fun get(groupTitle: String, isSession: Boolean): Schedule? {
        val folder = App.context!!.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(groupTitle)
            .resolve(if (isSession) SCHEDULE_SESSION_FOLDER else SCHEDULE_REGULAR_FOLDER)

        if (!folder.exists()) {
            return null
        }
        var fileToRead: File? = null
        var fileToReadOld: File? = null
        for (file in folder.listFiles()!!) {
            val ext = file.extension
            if (ext == CurrentExtension) {
                fileToRead = file
            } else if (ext == OldExtension) {
                fileToReadOld = file
            }
        }

        if (fileToRead == null) {
            if (fileToReadOld == null) {
                return null
            }
            fileToRead = fileToReadOld
        }
        val date = LocalDateTime.parse(fileToRead.nameWithoutExtension, dateTimeFormatter)
        return try {
            localConverter.deserializeSchedule(fileToRead.readText(), isSession, date)
        } catch (e: Exception) {
            null
        }
    }

    fun set(schedule: Schedule) {
        val folder = App.context!!.filesDir
            .resolve(SCHEDULE_FOLDER)
            .resolve(schedule.group.title)
            .resolve(if (schedule.isSession) SCHEDULE_SESSION_FOLDER else SCHEDULE_REGULAR_FOLDER)
        if (folder.exists()) {
            val files = folder.listFiles()!!
            for (file in files) {
                if (file.extension == CurrentExtension) {
                    val newFile = File(folder.path)
                        .resolve(file.nameWithoutExtension + "." + OldExtension)
                    newFile.delete()
                    newFile.parentFile?.mkdirs()
                    file.copyTo(newFile)
                }
                file.delete()
            }
        }
        val file = folder
            .resolve(schedule.lastUpdate.format(dateTimeFormatter) + "." + CurrentExtension)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        val scheduleString = localConverter.serializeSchedule(schedule)
        file.writeText(scheduleString)
    }
}