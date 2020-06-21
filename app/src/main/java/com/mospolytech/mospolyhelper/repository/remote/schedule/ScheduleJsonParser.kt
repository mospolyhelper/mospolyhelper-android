package com.mospolytech.mospolyhelper.repository.remote.schedule

import android.util.Log
import com.beust.klaxon.*
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.models.schedule.*
import java.lang.Exception
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleJsonParser {
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

        private const val FIRST_MODULE_KEY = "fm"
        private const val SECOND_MODULE_KEY = "sm"
        private const val NO_MODULE_KEY = "no"

        private const val AUDITORIUM_TITLE_KEY = "title"
        private const val AUDITORIUM_COLOR_KEY = "color"

        private const val WEEK_DAY_NUMBER = 7

        // endregion

        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    fun parse(scheduleString: String, isSession: Boolean): Schedule {
        val parser: Parser = Parser.default()
        val json = parser.parse(StringBuilder(scheduleString)) as JsonObject
        val status = json.string(STATUS_KEY)
        if (status == STATUS_ERROR) {
            val message = json.string(MESSAGE_KEY)
            throw JsonParsingException(
                "Schedule was returned with error status. " +
                        "Message: \"${message ?: ""}\""
            )
        } else if (status != STATUS_OK) {
            val message = json.string(MESSAGE_KEY)
            Log.w(
                TAG, "Schedule does not have status \"$STATUS_OK\" both \"$STATUS_ERROR\". " +
                        "Message: \"${message ?: ""}\""
            )
        }
        val isByDate =
            json.boolean(IS_SESSION) ?: throw JsonParsingException("Key \"$IS_SESSION\" not found")

        val group = parseGroup(json.obj(GROUP_KEY))
        val dailySchedules = parseDailySchedules(json.obj(SCHEDULE_GRID_KEY), group, isByDate)

        return Schedule.Builder(
            dailySchedules = dailySchedules,
            group = group,
            isSession = isSession
        ).build()
    }

    private fun parseGroup(json: JsonObject?): Group {
        if (json == null) {
            Log.w(TAG, "GROUP_KEY \"$GROUP_KEY\" not found")
            return Group.empty
        }

        val title = json.string(GROUP_TITLE_KEY) ?: "".apply {
            Log.w(TAG, "GROUP_TITLE_KEY \"$GROUP_TITLE_KEY\" not found")
        }

        val course = json.int(GROUP_COURSE_KEY) ?: 0.apply {
            Log.w(TAG, "GROUP_COURSE_KEY \"$GROUP_COURSE_KEY\" not found")
        }

        val dateFrom = parseGroupDateFrom(json.string(GROUP_DATE_FROM_KEY))
        val dateTo = parseGroupDateTo(json.string(GROUP_DATE_TO_KEY))

        val isEvening = json.int(GROUP_EVENING_KEY) ?: 0.apply {
            Log.w(TAG, "GROUP_EVENING_KEY \"$GROUP_EVENING_KEY\" not found")
        }

        val comment = json.string(GROUP_COMMENT_KEY) ?: "".apply {
            Log.w(TAG, "GROUP_COMMENT_KEY \"$GROUP_COMMENT_KEY\" not found")
        }

        return Group(title, course, dateFrom, dateTo, isEvening == 1 , comment)
    }

    private fun parseGroupDateFrom(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "GROUP_DATE_FROM_KEY \"$GROUP_DATE_FROM_KEY\" not found")
            LocalDate.MIN
        } else try {
            LocalDate.parse(json, dateFormatter)
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of GROUP_DATE_FROM_KEY \"$GROUP_DATE_FROM_KEY\"")
            LocalDate.MIN
        }
    }

    private fun parseGroupDateTo(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "GROUP_DATE_TO_KEY \"$GROUP_DATE_TO_KEY\" not found")
            LocalDate.MAX
        } else try {
            LocalDate.parse(json, dateFormatter)
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of GROUP_DATE_TO_KEY \"$GROUP_DATE_TO_KEY\"")
            LocalDate.MAX
        }
    }

    private fun parseDailySchedules(
        json: JsonObject?,
        group: Group,
        isByDate: Boolean
    ): List<List<Lesson>> {
        if (json == null) {
            throw JsonParsingException("SCHEDULE_GRID_KEY \"$SCHEDULE_GRID_KEY\" not found")
        }
        val tempList: List<MutableList<Lesson>> = listOf(
            mutableListOf(), mutableListOf(), mutableListOf(),
            mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf()
        )
        for ((day, dailySchedule) in json) {
            if (dailySchedule !is JsonObject || dailySchedule.isEmpty()) continue

            var parsedDay: Int
            var date = LocalDate.now()

            if (isByDate) {
                date = LocalDate.parse(day, dateFormatter) ?: continue
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
                if (lessonPlace !is JsonArray<*> || lessonPlace.isEmpty()) continue
                val parsedOrder = index.toInt() - 1
                for (lesson in lessonPlace) {
                    if (lesson !is JsonObject) continue
                    val parsedLesson = parseLesson(lesson, parsedOrder, group, isByDate, date)
                    tempList[parsedDay].add(parsedLesson)
                }
            }
        }
        return tempList
    }

    private fun parseLesson(
        json: JsonObject, order: Int,
        group: Group, isByDate: Boolean,
        date: LocalDate
    ): Lesson {
        val title = json.string(LESSON_TITLE_KEY)
            ?: "Не найден ключ названия занятия. Возможно, структура расписания была обновлена: $json"

        val teachers = parseTeachers(json.string(LESSON_TEACHER_KEY))

        var dateFrom = if (isByDate) date else parseDateFrom(json.string(LESSON_DATE_FROM_KEY))
        var dateTo = if (isByDate) date else parseDateTo(json.string(LESSON_DATE_TO_KEY))

        if (dateTo < dateFrom) {
            val buf = dateTo
            dateTo = dateFrom
            dateFrom = buf
        }

        val auditoriums = parseAuditoriums(json.array(LESSON_AUDITORIUMS_KEY))

        val type = json.string(LESSON_TYPE_KEY) ?: "".apply {
            Log.w(TAG, "LESSON_TYPE_KEY \"$LESSON_TYPE_KEY\" not found")
        }

        return Lesson(
            order, title, teachers,
            dateFrom, dateTo,
            auditoriums,
            Lesson.fixType(type, title), group
        )
    }

    private fun parseDateFrom(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "LESSON_DATE_FROM_KEY \"$LESSON_DATE_FROM_KEY\" not found")
            LocalDate.MIN
        } else try {
            LocalDate.parse(json, dateFormatter)
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of LESSON_DATE_FROM_KEY \"$LESSON_DATE_FROM_KEY\"")
            LocalDate.MIN
        }
    }

    private fun parseDateTo(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "LESSON_DATE_TO_KEY \"$LESSON_DATE_TO_KEY\" not found")
            LocalDate.MAX
        } else try {
            LocalDate.parse(json, dateFormatter)
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of LESSON_DATE_TO_KEY \"$LESSON_DATE_TO_KEY\"")
            LocalDate.MAX
        }
    }

    private fun parseTeachers(json: String?) = json?.run {
        split(',')
            .filter { it.isNotEmpty() }
            .map { Teacher.fromFullName(it) }
    } ?: emptyList()


    private fun parseAuditoriums(json: JsonArray<JsonObject>?): List<Auditorium> {
        if (json == null || json.isEmpty()) return listOf()

        val tempList = mutableListOf<Auditorium>()
        for (auditorium in json) {
            val name = auditorium.string(AUDITORIUM_TITLE_KEY) ?: continue
            val color = auditorium.string(AUDITORIUM_COLOR_KEY) ?: ""
            tempList.add((Auditorium(name, color)))
        }
        return tempList
    }
}