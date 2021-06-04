package com.mospolytech.mospolyhelper.data.schedule.converter

import android.util.Log
import com.mospolytech.mospolyhelper.data.utils.*
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium
import com.mospolytech.mospolyhelper.domain.schedule.model.group.Group
import com.mospolytech.mospolyhelper.domain.schedule.model.group.GroupInfo
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTypeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.canMergeByGroup
import com.mospolytech.mospolyhelper.domain.schedule.utils.mergeByGroup
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ScheduleFullRemoteConverter {
    companion object {
        // region Constants
        private const val STATUS_KEY = "status"
        private const val STATUS_ERROR = "error"
        private const val IS_SESSION = "isSession"
        private const val GROUP_KEY = "group"
        private const val SCHEDULE_GRID_KEY = "grid"

        private const val GROUP_TITLE_KEY = "title"
        private const val GROUP_DATE_FROM_KEY = "dateFrom"
        private const val GROUP_DATE_TO_KEY = "dateTo"
        private const val GROUP_EVENING_KEY = "evening"
        private const val GROUP_COMMENT_KEY = "comment"
        private const val GROUP_COURSE_KEY = "course"

        private const val LESSON_TITLE_KEY = "sbj"
        private const val LESSON_TEACHER_KEY = "teacher"
        private const val LESSON_DATE_FROM_KEY = "df"
        private const val LESSON_DATE_TO_KEY = "dt"
        private const val LESSON_AUDITORIUMS_KEY = "auditories"
        private const val LESSON_TYPE_KEY = "type"
        private const val LESSON_URL = "el"

        private const val AUDITORIUM_TITLE_KEY = "title"
        private const val AUDITORIUM_COLOR_KEY = "color"

        // endregion

        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    fun parseSchedules(
        semester: String,
        session: String,
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        groupCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>
    ): Schedule {
        val tempList = List(7) { mutableMapOf<LessonTime, MutableList<Lesson>>() }

        val json0 = Json.parseToJsonElement(semester)
        val contents0 = json0.array("contents")
        contents0?.forEach {
            parse(
                it,
                tempList,
                titleCollection,
                typeCollection,
                teacherCollection,
                groupCollection,
                auditoriumCollection
            )
        }
        val json1 = Json.parseToJsonElement(session)
        val contents1 = json1.array("contents")
        contents1?.forEach {
            parse(
                it,
                tempList,
                titleCollection,
                typeCollection,
                teacherCollection,
                groupCollection,
                auditoriumCollection
            )
        }

        val dailySchedules = tempList
            .map { it.map { LessonPlace(it.value.apply { sort() }, it.key) }
            .sortedBy { it.time } }
        return Schedule.from(dailySchedules)
    }

    private fun parse(
        json: JsonElement,
        dailySchedules: List<MutableMap<LessonTime, MutableList<Lesson>>>,
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        groupCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>
    ) {
        val status = json.string(STATUS_KEY)
        if (status == STATUS_ERROR) return
        val isByDate = json.boolean(IS_SESSION) ?: return

        val groupInfo = parseGroup(json.jsonObject[GROUP_KEY])
        groupCollection.add(groupInfo.group.title)
        parseDailySchedules(
            json.jsonObject[SCHEDULE_GRID_KEY], groupInfo.group, isByDate, dailySchedules,
            titleCollection, typeCollection,
            teacherCollection, auditoriumCollection
        )
    }

    private fun parseGroup(json: JsonElement?): GroupInfo {
        if (json == null) {
            Log.w(TAG, "GROUP_KEY \"$GROUP_KEY\" not found")
            return GroupInfo.empty
        }

        val title = json.string(GROUP_TITLE_KEY) ?: "".apply {
            Log.w(TAG, "GROUP_TITLE_KEY \"$GROUP_TITLE_KEY\" not found")
        }

        val course = json.int(GROUP_COURSE_KEY) ?: 0.apply {
            Log.w(TAG, "GROUP_COURSE_KEY \"$GROUP_COURSE_KEY\" not found")
        }

        val dateFrom = parseDate(json.string(GROUP_DATE_FROM_KEY), LocalDate.MIN)
        val dateTo = parseDate(json.string(GROUP_DATE_TO_KEY), LocalDate.MAX)

        val isEvening = json.int(GROUP_EVENING_KEY) ?: 0.apply {
            Log.w(TAG, "GROUP_EVENING_KEY \"$GROUP_EVENING_KEY\" not found")
        }

        val comment = json.string(GROUP_COMMENT_KEY) ?: "".apply {
            Log.w(TAG, "GROUP_COMMENT_KEY \"$GROUP_COMMENT_KEY\" not found")
        }

        return GroupInfo(
            Group(title, isEvening == 1),
            course,
            dateFrom,
            dateTo,
            comment
        )
    }

    private fun parseDailySchedules(
        json: JsonElement?,
        group: Group,
        isByDate: Boolean,
        dailySchedules: List<MutableMap<LessonTime, MutableList<Lesson>>>,
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>
    ) {
        if (json == null) return

        for ((day, dailySchedule) in json.jsonObject) {
            if (dailySchedule !is JsonObject || dailySchedule.isEmpty()) continue

            var parsedDay: Int
            var date = LocalDate.now()

            if (isByDate) {
                date = LocalDate.parse(
                    day,
                    dateFormatter
                ) ?: continue
                parsedDay = date.dayOfWeek.value
            } else {
                parsedDay = day.toInt()
            }
            // DayOfWeek 1..7
            if (parsedDay !in 1..7) {
                Log.w(TAG, "Parsed day out of range 1..7. Value: $day")
                continue
            }
            parsedDay %= 7

            for ((index, lessonPlace) in dailySchedule) {
                if (lessonPlace !is JsonArray || lessonPlace.isEmpty()) continue

                val parsedOrder = index.toInt() - 1
                val time = LessonTime(parsedOrder, group.isEvening)
                var lessons = dailySchedules[parsedDay][time]
                if (lessons == null) {
                    lessons = mutableListOf<Lesson>()
                    dailySchedules[parsedDay][time] = lessons
                }

                for (lesson in lessonPlace) {
                    if (lesson !is JsonObject) continue
                    val parsedLesson = parseLesson(
                        lesson, group, isByDate, date,
                        titleCollection, typeCollection,
                        teacherCollection, auditoriumCollection
                    )
                    val indexToMerge = lessons.indexOfFirst { it.canMergeByGroup(parsedLesson) }
                    if (indexToMerge == -1) {
                        lessons.add(parsedLesson)
                    } else {
                        lessons[indexToMerge] = lessons[indexToMerge].mergeByGroup(parsedLesson)
                    }
                }
            }
        }
    }

    private fun parseLesson(
        json: JsonElement,
        group: Group,
        isByDate: Boolean,
        date: LocalDate,
        titleCollection: MutableCollection<String>,
        typeCollection: MutableCollection<String>,
        teacherCollection: MutableCollection<String>,
        auditoriumCollection: MutableCollection<String>
    ): Lesson {
        val title = processTitle(
            json.string(LESSON_TITLE_KEY)
                ?: "Не найден ключ названия занятия. Возможно, структура расписания была обновлена: $json"
        )
        titleCollection.add(title)

        val teachers = json.string(LESSON_TEACHER_KEY)?.let { parseTeachers(it) } ?: emptyList()
        teachers.forEach { teacherCollection.add(it.name) }

        var dateFrom =
            if (isByDate) date else parseDate(json.string(LESSON_DATE_FROM_KEY), LocalDate.MIN)
        var dateTo =
            if (isByDate) date else parseDate(json.string(LESSON_DATE_TO_KEY), LocalDate.MAX)

        if (dateTo < dateFrom) {
            val buf = dateTo
            dateTo = dateFrom
            dateFrom = buf
        }

        val auditoriums = parseAuditoriums(
            json.array(LESSON_AUDITORIUMS_KEY),
            json.stringOrNull(LESSON_URL) ?: "",
            auditoriumCollection
        )

        var type = json.string(LESSON_TYPE_KEY) ?: "".apply {
            Log.w(TAG, "LESSON_TYPE_KEY \"$LESSON_TYPE_KEY\" not found")
        }
        type = LessonTypeUtils.fixType(
            type,
            title
        )
        typeCollection.add(type)

        return Lesson(
            title,
            type,
            teachers,
            auditoriums,
            listOf(group),
            dateFrom,
            dateTo
        )
    }

    private fun parseDate(date: String?, default: LocalDate): LocalDate {
        return if (date == null) {
            default
        } else try {
            LocalDate.parse(date, dateFormatter)
        } catch (e: DateTimeParseException) {
            Log.w(TAG, "Can not parse value LocalDate", e)
            default
        }
    }


    private fun parseAuditoriums(json: JsonArray?, url: String, auditoriumCollection: MutableCollection<String>): List<Auditorium> =
        json?.mapNotNull {
            val title = it.string(AUDITORIUM_TITLE_KEY) ?: return@mapNotNull null
            val color = it.string(AUDITORIUM_COLOR_KEY) ?: ""
            val aud = processAuditorium(title, color, url)
            auditoriumCollection.add(aud.title)
            aud
        } ?: emptyList()
}