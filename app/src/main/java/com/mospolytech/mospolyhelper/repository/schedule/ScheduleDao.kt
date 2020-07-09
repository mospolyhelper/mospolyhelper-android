package com.mospolytech.mospolyhelper.repository.schedule

import android.util.Log
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.utils.TAG
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ScheduleDao(
    private val converter: ScheduleConverter = ScheduleConverter(),
    private val client: ScheduleClient = ScheduleClient(),
    private val scheduleParser: ScheduleJsonParser = ScheduleJsonParser()
) {

    companion object {
        const val CurrentExtension = "current"
        const val OldExtension = "backup"
        const val CustomExtension = "custom"
        const val SCHEDULE_FOLDER = "cached_schedules"
        const val GROUP_LIST_FOLDER = "cached_group_list"
        const val SCHEDULE_SESSION_FOLDER = "session"
        const val SCHEDULE_REGULAR_FOLDER = "regular"
        const val GROUP_LIST_FILE = "group_list"
    }
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    suspend fun download(groupTitle: String, isSession: Boolean): Schedule? {
        return try {
            val scheduleString = client.getSchedule(groupTitle, isSession)
            scheduleParser.parse(scheduleString, isSession)
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: groupTitle: $groupTitle, isSession: $isSession", e)
            null
        }
    }

    fun read(groupTitle: String, isSession: Boolean): Schedule? {
        val folder = App.context.filesDir
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
            converter.deserializeSchedule(fileToRead.readText(), isSession, date)
        } catch (e: Exception) {
            null
        }
    }

    fun save(schedule: Schedule) {
        val folder = App.context.filesDir
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
        val scheduleString = converter.serializeSchedule(schedule)
        file.writeText(scheduleString)
    }

    suspend fun update(group: String, isSession: Boolean): Schedule? {
        val schedule: Schedule? = download(group, isSession)
        if (schedule == null) {
            return schedule
        }
        try {
            save(schedule)
        } catch (e: Exception) {
        }
        return schedule
    }
}