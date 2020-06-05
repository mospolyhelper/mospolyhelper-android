package com.mospolytech.mospolyhelper.repository.dao

import android.util.Log
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.local.converters.ScheduleConverter
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.repository.remote.schedule.GroupListJsonParser
import com.mospolytech.mospolyhelper.repository.remote.schedule.ScheduleClient
import com.mospolytech.mospolyhelper.repository.remote.schedule.ScheduleJsonParser
import com.mospolytech.mospolyhelper.utils.ContextProvider
import kotlinx.coroutines.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashSet


class ScheduleDao {

    companion object {
        const val CurrentExtension = ".current"
        const val OldExtension = ".backup"
        const val CustomExtension = ".custom"
        const val SCHEDULE_FOLDER = "cached_schedules"
        const val GROUP_LIST_FOLDER = "cached_group_list"
        const val SCHEDULE_SESSION_FOLDER = "session"
        const val SCHEDULE_REGULAR_FOLDER = "regular"
        const val GROUP_LIST_FILE = "group_list"
    }
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    val converter = ScheduleConverter()
    val client = ScheduleClient()
    val scheduleParser = ScheduleJsonParser()
    val groupListParser = GroupListJsonParser()

    suspend fun getGroupList(): List<String> {
        val groupListString = client.getGroupList()
        return groupListParser.parseGroupList(groupListString)
    }

    suspend fun getGroupList2(downloadNew: Boolean): List<String> {
        var groupList: List<String>? = null
        if (downloadNew) {
            try {
                groupList = getGroupList()
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

    suspend fun getSchedule(groupTitle: String, isSession: Boolean): Schedule {
        val scheduleString = client.getSchedule(groupTitle, isSession)
        val schedule = scheduleParser.parse(scheduleString, isSession)
        return schedule
    }

    suspend fun getSchedule2(group: String, isSession: Boolean, downloadNew: Boolean): Schedule? {
        var schedule: Schedule? = null
        if (downloadNew) {
            try {
                schedule = getSchedule(group, isSession)
                //if (schedule == null) {
                   // Announce?.Invoke(StringProvider.GetString(StringId.ScheduleWasntFounded))
                //}
                try {
                    saveSchedule(schedule)
                } catch (ex: Exception) {
                    Log.e(TAG, "", ex)
                }
            } catch (ex1: Exception) {
                // this.logger.Error(ex1, "Download schedule error")
                try {
                    // Announce?.Invoke(StringProvider.GetString(StringId.ScheduleWasntFounded))
                    schedule = readSchedule(group, isSession)!! // TODO: Fix
                    // throw Exception("Read schedule from storage fail")
                    // Announce.Invoke(StringProvider.GetString(StringId.OfflineScheduleWasFounded))
                } catch (ex2: Exception) {
                    // this.logger.Error(ex2, "Read schedule after download failed error")
                    // Announce?.Invoke(StringProvider.GetString(StringId.OfflineScheduleWasntFounded))
                    schedule = null
                }
            }
        } else {
            //if (schedule != null && this.Schedule.Group.Title == group && this.Schedule.IsSession == isSession) {
            //   return this.Schedule;
            //}
            try
            {
                schedule = readSchedule(group, isSession)
                //if (schedule == null) {
                //    throw new Exception("Read schedule from storage fail");
                //}
                //if (this.Schedule.Version != Schedule.RequiredVersion) {
                //    throw new Exception("Read schedule from storage fail");
                //}
            } catch (ex1: Exception) {
                //this.logger.Error(ex1, "Read schedule error");
                //Announce?.Invoke(StringProvider.GetString(StringId.OfflineScheduleWasntFounded));
            }
        }

        if (schedule == null) {
            return null
        }
        if (group != schedule.group.title) {
            //this.logger.Warn("{group} != {scheduleGroupTitle}", group, this.Schedule?.Group?.Title);
        }
        return schedule
        // TODO: Rewrite this
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
        val schedule = converter.deserializeSchedule(fileToRead.readText(), isSession, date)
        return schedule
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
                        .resolve(file.nameWithoutExtension + OldExtension)
                    newFile.delete()
                    newFile.parentFile?.mkdirs()
                    file.copyTo(newFile)
                }
                file.delete()
            }
        }
        val file = folder
            .resolve(schedule.lastUpdate.format(dateTimeFormatter) + CurrentExtension)
        file.delete()
        file.parentFile?.mkdirs()
        file.createNewFile()
        val scheduleString = converter.serializeSchedule(schedule)
        file.writeText(scheduleString)
    }

    var scheduleCounter = AtomicInteger(0)

    suspend fun getSchedules(groupList: List<String>): SchedulePackList? {
        if (groupList.isEmpty()) {
            return null
        }

        scheduleCounter.set(0)
        val maxCount = groupList.size * 3 + groupList.size / 33
        val packs = mutableListOf<SchedulePack>()
        val deferredList = mutableListOf<Deferred<Unit>>()

        val chunks = groupList.chunked(groupList.size / (Runtime.getRuntime().availableProcessors() * 3))
        for (chunk in chunks) {
            for (groupTitle in chunk) {
                deferredList.add(GlobalScope.async<Unit> {
                    scheduleCounter.incrementAndGet()
                    // lock (this.key)
                    // {
                    //   DownloadProgressChanged?.Invoke(this.scheduleCounter * 10000 / maxCount);
                    //  }
                    try {
                        val schedule = getSchedule(groupTitle, false)
                        val data = allDataFromSchedule(schedule)
                        synchronized(packs) {
                            packs.add(data)
                        }
                    } catch (ex: Exception) {}
                    Log.d(TAG, (scheduleCounter.incrementAndGet() * 10000 / maxCount).toString())
                    // lock (this.key)
                    // {
                    //     DownloadProgressChanged?.Invoke(this.scheduleCounter * 10000 / maxCount);
                    // }

                    try {
                        val schedule = getSchedule(groupTitle, true)
                        val data = allDataFromSchedule(schedule)
                        synchronized(packs) {
                            packs.add(data)
                        }
                    }
                    catch (ex: Exception) {}
                    Log.d(TAG, (scheduleCounter.incrementAndGet() * 10000 / maxCount).toString())
                    // lock (this.key)
                    // {
                    //     DownloadProgressChanged?.Invoke(this.scheduleCounter * 10000 / maxCount);
                    // }
                })
            }
        }
        deferredList.awaitAll()

        var packList = SchedulePackListTemp(
            emptySequence(),
            emptySequence(),
            emptySequence(),
            emptySequence(),
            emptySequence()
        )
        packList = packs.fold(packList) { acc, e ->
            acc.schedules += e.schedule
            acc.lessonTitles += e.lessonTitles
            acc.lessonTeachers += e.lessonTeachers
            acc.lessonAuditoriums += e.lessonAuditoriums
            acc.lessonTypes += e.lessonTypes
            acc
        }

        return SchedulePackList(
            packList.schedules.toList(),
            packList.lessonTitles.toSortedSet(),
            packList.lessonTeachers.toSortedSet(),
            packList.lessonAuditoriums.toSortedSet(),
            packList.lessonTypes.toSortedSet()
        )
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
        val schedules: List<Schedule>,
        val lessonTitles: Set<String>,
        val lessonTeachers: Set<String>,
        val lessonAuditoriums: Set<String>,
        val lessonTypes: Set<String>
    )


    class SchedulePackListTemp(
        var schedules: Sequence<Schedule>,
        var lessonTitles: Sequence<String>,
        var lessonTeachers: Sequence<String>,
        var lessonAuditoriums: Sequence<String>,
        var lessonTypes: Sequence<String>
    )

    class SchedulePack(
        val schedule: Schedule,
        val lessonTitles: Set<String>,
        val lessonTeachers: Set<String>,
        val lessonAuditoriums: Set<String>,
        val lessonTypes: Set<String>
    )
}