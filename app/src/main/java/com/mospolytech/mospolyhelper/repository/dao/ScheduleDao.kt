package com.mospolytech.mospolyhelper.repository.dao

import android.util.Log
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.local.converters.ScheduleConverter
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.repository.remote.schedule.GroupListJsonParser
import com.mospolytech.mospolyhelper.repository.remote.schedule.ScheduleClient
import com.mospolytech.mospolyhelper.repository.remote.schedule.ScheduleJsonParser
import com.mospolytech.mospolyhelper.utils.ContextProvider
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashSet


class ScheduleDao {

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

    private val converter = ScheduleConverter()
    private val client = ScheduleClient()
    private val scheduleParser = ScheduleJsonParser()
    private val groupListParser = GroupListJsonParser()

    private suspend fun downloadGroupList(): List<String> {
        val groupListString = client.getGroupList()
        return groupListParser.parseGroupList(groupListString)
    }

    suspend fun getGroupList2(downloadNew: Boolean): List<String> {
        var groupList: List<String>? = null
        if (downloadNew) {
            try {
                groupList = downloadGroupList()
                try {
                    saveGroupList(groupList)
                } catch (ex: Exception) {
                    //this.logger.Error(ex, "Saving group list error");
                }
            } catch (ex1: Exception) {
                //this.logger.Error(ex1, "Download group list error");
                try {
                    //Announce?.Invoke(StringProvider.GetString(StringId.GroupListWasntFounded));
                    groupList = readGroupList()
                    if (groupList.isEmpty()) {
                        throw Exception("Read group list from storage fail");
                    }
                    //Announce.Invoke(StringProvider.GetString(StringId.OfflineGroupListWasFounded));
                } catch (ex2: Exception) {
                    //this.logger.Error(ex2, "Read group lsit after download failed error");
                    //Announce?.Invoke(StringProvider.GetString(StringId.OfflineGroupListWasntFounded));
                    groupList = emptyList()
                }
            }
        }
        return groupList ?: emptyList()
    }

    private fun readGroupList(): List<String> {
        val file = ContextProvider.getFilesDir()
            .resolve(GROUP_LIST_FOLDER)
            .resolve(GROUP_LIST_FILE)
        return converter.deserializeGroupList(file.readText())
    }

    private fun saveGroupList(groupList: List<String>) {
        val file = ContextProvider.getFilesDir()
            .resolve(GROUP_LIST_FOLDER)
            .resolve(GROUP_LIST_FILE)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        file.writeText(converter.serializeGroupList(groupList))
    }

    private suspend fun downloadSchedule(groupTitle: String, isSession: Boolean): Schedule? {
        return try {
            val scheduleString = client.getSchedule(groupTitle, isSession)
            scheduleParser.parse(scheduleString, isSession)
        } catch (e: Exception) {
            Log.e(TAG, "Schedule downloading and parsing error: groupTitle: $groupTitle, isSession: $isSession", e)
            null
        }
    }

    suspend fun getSchedule2(
        group: String,
        isSession: Boolean,
        downloadNew: Boolean,
        messageBlock: (String) -> Unit = { }
    ): Schedule? {
        var schedule: Schedule? = null
        if (downloadNew) {
            try {
                schedule = updateSchedule(group, isSession)
                if (schedule == null) {
                    messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
                }
            } catch (e1: Exception) {
                // this.logger.Error(ex1, "Download schedule error")
                try {
                    messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
                    schedule = readSchedule(group, isSession)!! // TODO: Fix
                    // throw Exception("Read schedule from storage fail")
                    messageBlock(StringProvider.getString(StringId.OfflineScheduleWasFound))
                } catch (e2: Exception) {
                    // this.logger.Error(ex2, "Read schedule after download failed error")
                    messageBlock(StringProvider.getString(StringId.OfflineScheduleWasntFound))
                    schedule = null
                }
            }
        } else {
            try {
                schedule = readSchedule(group, isSession)
            } catch (e: Exception) {
                //this.logger.Error(ex1, "Read schedule error");
                messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
            }
        }
        return schedule
    }

    fun readSchedule(groupTitle: String, isSession: Boolean): Schedule? {
        val folder = ContextProvider.getFilesDir()
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
        val t = fileToRead.readText()
        val q = try {
            converter.deserializeSchedule(t, isSession, date)
        } catch (e: Exception) {
            null
        }
        return q
    }

    fun saveSchedule(schedule: Schedule) {
        val folder = ContextProvider.getFilesDir()
            .resolve(SCHEDULE_FOLDER)
            .resolve(schedule.group.title)
            .resolve(if (schedule.isSession) SCHEDULE_SESSION_FOLDER else SCHEDULE_REGULAR_FOLDER)
        if (folder.exists()) {
            val files = folder.listFiles()!!
            for (file in files) {
                if (file.extension == CurrentExtension) {
                    val newFile = File(folder.path)
                        .resolve(file.nameWithoutExtension + "." +  OldExtension)
                    newFile.delete()
                    newFile.parentFile?.mkdirs()
                    file.copyTo(newFile)
                }
                file.delete()
            }
        }
        val file = folder
            .resolve(schedule.lastUpdate.format(dateTimeFormatter) + "." +  CurrentExtension)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        val scheduleString = converter.serializeSchedule(schedule)
        file.writeText(scheduleString)
    }

    suspend fun updateSchedule(group: String, isSession: Boolean): Schedule? {
        val schedule: Schedule? = downloadSchedule(group, isSession)
        if (schedule == null) {
            return schedule
        }
        try {
            saveSchedule(schedule)
        } catch (e: Exception) {
            Log.e("ScheduleDao", "!!!!", e)
        }
        return schedule
    }

    var scheduleCounter = AtomicInteger(0)

    suspend fun getSchedules(groupList: List<String>, onProgressChanged: (Float) -> Unit): SchedulePackList? = coroutineScope {
        if (groupList.isEmpty()) {
            return@coroutineScope null
        }

        scheduleCounter.set(0)
        val maxProgress = groupList.size * 4

        val chunkSize = groupList.size / (Runtime.getRuntime().availableProcessors() * 3)
        val chunks = if (chunkSize > 3) groupList.chunked(chunkSize) else listOf(groupList)

        val channel = Channel<Schedule?>()
        val deferredList = chunks.map { chunk ->
            async(context = Dispatchers.IO) {
                for (groupTitle in chunk) {
                    channel.send(updateSchedule(groupTitle, false))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
                for (groupTitle in chunk) {
                    channel.send(updateSchedule(groupTitle, true))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
            }
        }

        launch {
            deferredList.awaitAll()
            channel.close()
        }

        val packList = SchedulePackList(
            ScheduleIterable(groupList),
            sortedSetOf(),
            sortedSetOf(),
            sortedSetOf(),
            sortedSetOf()
        )
        channel.receiveAsFlow().collect {
            onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
            if (it == null) {
                return@collect
            }
            for (dailySchedule in it.dailySchedules) {
                for (lesson in dailySchedule) {
                    packList.lessonTitles.add(lesson.title)
                    for (teacher in lesson.teachers) {
                        packList.lessonTeachers.add(teacher.getFullName())
                    }
                    for (auditorium in lesson.auditoriums) {
                        packList.lessonAuditoriums.add(auditorium.title)
                    }
                    packList.lessonTypes.add(lesson.type)
                }
            }
        }
        return@coroutineScope packList
    }

    fun allDataFromSchedule(schedule: Schedule): SchedulePack {
        val lessonTitles = HashSet<String>()
        val lessonTeachers = HashSet<String>()
        val lessonAuditoriums = HashSet<String>()
        val lessonTypes = HashSet<String>()
        for (dailySchedule in schedule.dailySchedules) {
            for (lesson in dailySchedule) {
                lessonTitles.add(lesson.title)
                for (teacher in lesson.teachers) {
                    lessonTeachers.add(teacher.getFullName())
                }
                if (lesson.auditoriums.isNotEmpty()) {
                    for (auditorium in lesson.auditoriums) {
                        lessonAuditoriums.add(auditorium.title)
                    }
                }
                lessonTypes.add(lesson.type)
            }
        }
        return SchedulePack(
            schedule,
            lessonTitles,
            lessonTeachers,
            lessonAuditoriums,
            lessonTypes
        )
    }

    class SchedulePackList(
        val schedules: Iterable<Schedule?>,
        val lessonTitles: MutableSet<String>,
        val lessonTeachers: MutableSet<String>,
        val lessonAuditoriums: MutableSet<String>,
        val lessonTypes: MutableSet<String>
    )

    class SchedulePack(
        val schedule: Schedule,
        val lessonTitles: Set<String>,
        val lessonTeachers: Set<String>,
        val lessonAuditoriums: Set<String>,
        val lessonTypes: Set<String>
    )
}

class ScheduleIterable(
    private val groupList: Iterable<String>
): Iterable<Schedule?> {
    private val dao = ScheduleDao()

    override fun iterator(): Iterator<Schedule?> {
        return ScheduleIterator(groupList.iterator(), dao)
    }

    class ScheduleIterator(
        private var groupListIterator: Iterator<String>,
        private var dao: ScheduleDao
    ): Iterator<Schedule?> {
        private var isSessionFlag = false
        private var curGroupTitle = ""

        override fun hasNext() = groupListIterator.hasNext() || isSessionFlag

        override fun next() = try {
            if (isSessionFlag) {
                val result = dao.readSchedule(curGroupTitle, isSessionFlag)
                isSessionFlag = false
                result
            } else {
                curGroupTitle = groupListIterator.next()
                val result = dao.readSchedule(curGroupTitle, isSessionFlag)
                isSessionFlag = true
                result
            }
        } catch (e: Exception) {
            null
        }
    }
}