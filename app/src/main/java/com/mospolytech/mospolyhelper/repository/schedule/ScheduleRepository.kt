package com.mospolytech.mospolyhelper.repository.schedule

import android.util.Log
import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.repository.schedule.utils.ScheduleIterable
import com.mospolytech.mospolyhelper.utils.StringId
import com.mospolytech.mospolyhelper.utils.StringProvider
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicInteger

class ScheduleRepository(
    private val dao: ScheduleDao
) {
    suspend fun getSchedule(
        group: String,
        isSession: Boolean,
        downloadNew: Boolean,
        messageBlock: (String) -> Unit = { }
    ): Schedule? {
        var schedule: Schedule? = null
        if (downloadNew) {
            try {
                schedule = dao.update(group, isSession)
                if (schedule == null) {
                    messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
                }
            } catch (e1: Exception) {
                Log.e(TAG, "Download schedule fail", e1)
                try {
                    messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
                    schedule = dao.read(group, isSession)
                    messageBlock(StringProvider.getString(StringId.OfflineScheduleWasFound))
                } catch (e2: Exception) {
                    Log.e(TAG, "Read schedule after download fail", e2)
                    messageBlock(StringProvider.getString(StringId.OfflineScheduleWasntFound))
                    schedule = null
                }
            }
        } else {
            try {
                schedule = dao.read(group, isSession)
            } catch (e: Exception) {
                Log.e(TAG, "Read schedule error", e)
                messageBlock(StringProvider.getString(StringId.ScheduleWasntFound))
            }
        }
        return schedule
    }

    private val scheduleCounter = AtomicInteger(0)

    suspend fun getAnySchedules(groupList: List<String>, onProgressChanged: (Float) -> Unit): SchedulePackList = coroutineScope {
        if (groupList.isEmpty()) {
            return@coroutineScope SchedulePackList(
                emptyList(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf(),
                mutableSetOf()
            )
        }

        scheduleCounter.set(0)
        val maxProgress = groupList.size * 4

        val chunkSize = groupList.size / (Runtime.getRuntime().availableProcessors() * 3)
        val chunks = if (chunkSize > 3) groupList.chunked(chunkSize) else listOf(groupList)

        val channel = Channel<Schedule?>()
        val deferredList = chunks.map { chunk ->
            async(context = Dispatchers.IO) {
                for (groupTitle in chunk) {
                    channel.send(dao.update(groupTitle, false))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
                for (groupTitle in chunk) {
                    channel.send(dao.update(groupTitle, true))
                    onProgressChanged(scheduleCounter.incrementAndGet().toFloat() / maxProgress)
                }
            }
        }

        launch {
            deferredList.awaitAll()
            channel.close()
        }

        val packList =
            SchedulePackList(
                ScheduleIterable(
                    groupList
                ),
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