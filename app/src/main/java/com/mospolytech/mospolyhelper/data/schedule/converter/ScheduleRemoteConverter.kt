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
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class ScheduleRemoteConverter {
    companion object {
        // region Constants
        private const val STATUS_KEY = "status"
        private const val STATUS_OK = "ok"
        private const val STATUS_ERROR = "error"
        private const val MESSAGE_KEY = "message"
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
        private const val LESSON_WEBINAR_LINK_KEY = "wl"
        private const val LESSON_WEEK_KEY = "week"
        private const val LESSON_URL = "el"

        private const val FIRST_MODULE_KEY = "fm"
        private const val SECOND_MODULE_KEY = "sm"
        private const val NO_MODULE_KEY = "no"

        private const val AUDITORIUM_TITLE_KEY = "title"
        private const val AUDITORIUM_COLOR_KEY = "color"

        private const val WEEK_DAY_NUMBER = 7

        // endregion

        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    fun parseSchedules(schedulesString: String): Sequence<Schedule> {
        val json = Json.parseToJsonElement(schedulesString)
        val contents = json.jsonObject["contents"]?.jsonArray ?: return emptySequence()
        return contents.iterator().asSequence().mapNotNull {
            try {
                parse(it.toString())
            } catch (e: Exception) {
                Log.e(TAG, "Schedule (Download all) parsing exception", e)
                null
            }
        }
    }

    fun parse(scheduleString: String): Schedule {
        val json = Json.parseToJsonElement(scheduleString)
        val status = json.string(STATUS_KEY)
        if (status == STATUS_ERROR) {
            val message = json.string(MESSAGE_KEY)
            throw SerializationException(
                "Schedule was returned with error status. " +
                        "Message: \"${message ?: ""}\""
            )
        } else if (status != STATUS_OK) {
            val message = json.string(MESSAGE_KEY)
//            Log.w(
//                TAG, "Schedule does not have status \"$STATUS_OK\" both \"$STATUS_ERROR\". " +
//                        "Message: \"${message ?: ""}\""
//            )
        }
        val isByDate =
            json.boolean(IS_SESSION)
                ?: throw SerializationException("Key \"$IS_SESSION\" not found")

        val groupInfo = parseGroup(json.jsonObject[GROUP_KEY])
        val dailySchedules =
            parseDailySchedules(json.jsonObject[SCHEDULE_GRID_KEY], groupInfo.group, isByDate)

        return Schedule.from(dailySchedules)
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
        isByDate: Boolean
    ): List<List<LessonPlace>> {
        if (json == null) {
            throw SerializationException("SCHEDULE_GRID_KEY \"$SCHEDULE_GRID_KEY\" not found")
        }
        val tempList = List(7) { mutableListOf<LessonPlace>() }
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

                val lessons = mutableListOf<Lesson>()
                val parsedOrder = index.toInt() - 1
                for (lesson in lessonPlace) {
                    if (lesson !is JsonObject) continue
                    val parsedLesson = parseLesson(lesson, group, isByDate, date)
                    lessons.add(parsedLesson)
                }
                lessons.sort()
                tempList[parsedDay].add(
                    LessonPlace(
                        lessons,
                        LessonTime(parsedOrder, group.isEvening)
                    )
                )
            }
            tempList[parsedDay].sortBy { it.time }
        }
        return tempList
    }

    private fun parseLesson(
        json: JsonElement,
        group: Group,
        isByDate: Boolean,
        date: LocalDate
    ): Lesson {
        val title = processTitle(
            json.string(LESSON_TITLE_KEY)
                ?: "Не найден ключ названия занятия. Возможно, структура расписания была обновлена: $json"
        )

        val teachers = json.string(LESSON_TEACHER_KEY)?.let { parseTeachers(it) } ?: emptyList()

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
            json.stringOrNull(LESSON_URL) ?: ""
        )

        val type = json.string(LESSON_TYPE_KEY) ?: "".apply {
            Log.w(TAG, "LESSON_TYPE_KEY \"$LESSON_TYPE_KEY\" not found")
        }

        return Lesson(
            title,
            LessonTypeUtils.fixType(
                type,
                title
            ),
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


    private fun parseAuditoriums(json: JsonArray?, url: String): List<Auditorium> =
        json?.mapNotNull {
            val title = it.string(AUDITORIUM_TITLE_KEY) ?: return@mapNotNull null
            val color = it.string(AUDITORIUM_COLOR_KEY) ?: ""
            processAuditorium(title, color, url)
        } ?: emptyList()
}