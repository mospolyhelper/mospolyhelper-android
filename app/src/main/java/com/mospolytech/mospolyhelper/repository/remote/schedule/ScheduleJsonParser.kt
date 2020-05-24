package com.mospolytech.mospolyhelper.repository.remote.schedule

import android.util.Log
import com.beust.klaxon.*
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.models.schedule.*
import java.text.SimpleDateFormat
import java.util.*

class ScheduleJsonParser {
    companion object {
        // region Constants
        const val STATUS_KEY = "status";
        const val STATUS_OK = "ok"
        const val STATUS_ERROR = "error"
        const val MESSAGE_KEY = "message";
        const val IS_SESSION = "isSession";
        const val GROUP_KEY = "group";
        const val SCHEDULE_GRID_KEY = "grid";

        const val GROUP_TITLE_KEY = "title";
        const val GROUP_DATE_FROM_KEY = "dateFrom";
        const val GROUP_DATE_TO_KEY = "dateTo";
        const val GROUP_EVENING_KEY = "evening";
        const val GROUP_COMMENT_KEY = "comment";
        const val GROUP_COURSE_KEY = "course";

        const val LESSON_TITLE_KEY = "sbj";
        const val LESSON_TEACHER_KEY = "teacher";
        const val LESSON_DATE_FROM_KEY = "df";
        const val LESSON_DATE_TO_KEY = "dt";
        const val LESSON_AUDITORIUMS_KEY = "auditories";
        const val LESSON_TYPE_KEY = "type";
        const val LESSON_WEBINAR_LINK_KEY = "wl"
        const val LESSON_WEEK_KEY = "week";

        const val FIRST_MODULE_KEY = "fm";
        const val SECOND_MODULE_KEY = "sm";
        const val NO_MODULE_KEY = "no";

        const val AUDITORIUM_TITLE_KEY = "title";
        const val AUDITORIUM_COLOR_KEY = "color";

        const val WEEK_DAY_NUMBER = 7;

        // endregion

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    }

    fun parse(scheduleString: String, isSession: Boolean): Schedule {
        val parser: Parser = Parser.default()
        val json = parser.parse(scheduleString) as JsonObject
        val status = json.string(STATUS_KEY)
        if (status == STATUS_ERROR) {
            val message = json.string(MESSAGE_KEY)
            throw JsonParsingException("Schedule was returned with error status. " +
                    "Message: \"${message ?: ""}\"")
        } else if (status != STATUS_OK) {
            val message = json.string(MESSAGE_KEY)
            Log.w(TAG, "Suspicious behavior: " +
                    "Schedule does not have status \"$STATUS_OK\" both \"$STATUS_ERROR\". " +
                    "Message: \"${message ?: ""}\"")
        }
        val isByDate = json.boolean(IS_SESSION) ?:
            throw JsonParsingException("Key \"$IS_SESSION\" not found")

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
            Log.w(TAG, "Suspicious behavior: Group key \"$GROUP_KEY\" not found")
            return Group.empty
        }

        val title = json.string(GROUP_TITLE_KEY) ?: "".apply {
            Log.w(TAG, "Suspicious behavior: Group title key \"$GROUP_TITLE_KEY\" not found")
        }

        val course = json.int(GROUP_COURSE_KEY) ?: 0.apply {
            Log.w(TAG, "Suspicious behavior: Group course key \"$GROUP_COURSE_KEY\" not found")
        }

        val dateFrom = Calendar.getInstance().apply {
            val temp = json.string(GROUP_DATE_FROM_KEY)
            if (temp == null) {
                time = Date(Long.MIN_VALUE)
                Log.w(TAG, "Suspicious behavior: Group date from key \"$GROUP_DATE_FROM_KEY\" not found")
            } else {
                time = dateFormat.parse(temp) ?: Date(Long.MIN_VALUE).apply {
                    Log.w(TAG, "Suspicious behavior: " +
                            "Can not parse value of group date from key \"$GROUP_DATE_FROM_KEY\"")
                }
            }
        }
        val dateTo = Calendar.getInstance().apply {
            val temp = json.string(GROUP_DATE_TO_KEY)
            if (temp == null) {
                time = Date(Long.MAX_VALUE)
                Log.w(TAG, "Suspicious behavior: Group date to key \"$GROUP_DATE_TO_KEY\" not found")
            } else {
                time = dateFormat.parse(temp) ?: Date(Long.MAX_VALUE).apply {
                    Log.w(TAG, "Suspicious behavior: " +
                            "Can not parse value of group date to key \"$GROUP_DATE_TO_KEY\"")
                }
            }
        }

        val isEvening = json.boolean(GROUP_EVENING_KEY) ?: false.apply {
            Log.w(TAG, "Suspicious behavior: Group evening key \"$GROUP_EVENING_KEY\" not found")
        }

        val comment = json.string(GROUP_COMMENT_KEY) ?: "".apply {
            Log.w(TAG, "Suspicious behavior: Group comment key \"$GROUP_COMMENT_KEY\" not found")
        }

        return Group(title, course, dateFrom, dateTo, isEvening, comment)
    }

    private fun parseDailySchedules(json: JsonObject?, group: Group, isByDate: Boolean): List<List<Lesson>> {
        if (json == null) {
            throw JsonParsingException("Suspicious behavior: Schedule grid key \"$SCHEDULE_GRID_KEY\" not found")
        }
        val tempList: List<MutableList<Lesson>> = listOf(mutableListOf(), mutableListOf(), mutableListOf(),
                mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
        for ((day, dailySchedule) in json) {
            if (dailySchedule !is JsonObject || dailySchedule.isEmpty()) continue

            var parsedDay: Int
            var date = Calendar.getInstance()

            if (isByDate) {
                val parsedDate = dateFormat.parse(day) ?: continue
                date.time = parsedDate
                parsedDay = date.get(Calendar.DAY_OF_WEEK)
            } else {
                parsedDay = day.toInt()
            }
            // DayOfWeek 1..7
            if (parsedDay > 7 || parsedDay < 1) {
                Log.w(TAG, "Parsed day out of range 1..7. Value: $day")
                continue
            }
            parsedDay %= 7

            for ((index, lessonPlace) in dailySchedule) {
                if (lessonPlace !is JsonArray<*> || lessonPlace.isEmpty()) continue
                val parsedOrder = index.toInt() - 1
                for (lesson in lessonPlace) {
                    if (lesson !is JsonObject) continue
                    val lesson = parseLesson(lesson, parsedOrder, group, isByDate, date)
                    tempList[parsedDay].add(lesson)
                }
            }
        }
        return tempList
    }

    private fun parseLesson(
        json: JsonObject, order: Int,
        group: Group, isByDate: Boolean,
        date: Calendar
    ): Lesson {
        val title = json.string(LESSON_TITLE_KEY)
            ?: "Не найден ключ названия занятия. Возможно, структура расписания была обновлена: $json"

        val teachers = parseTeachers(json.string(LESSON_TEACHER_KEY))

        var dateFrom = if (isByDate) date else Calendar.getInstance().apply {
            val temp = json.string(LESSON_DATE_FROM_KEY)
            if (temp == null) {
                time = Date(Long.MIN_VALUE)
                Log.w(TAG, "Suspicious behavior: Lesson date from key \"$LESSON_DATE_FROM_KEY\" not found")
            } else {
                time = dateFormat.parse(temp) ?: Date(Long.MIN_VALUE).apply {
                    Log.w(TAG, "Suspicious behavior: " +
                            "Can not parse value of lesson date from key \"$LESSON_DATE_FROM_KEY\"")
                }
            }
        }
        var dateTo = if (isByDate) date else Calendar.getInstance().apply {
            val temp = json.string(LESSON_DATE_TO_KEY)
            if (temp == null) {
                time = Date(Long.MAX_VALUE)
                Log.w(TAG, "Suspicious behavior: Lesson date to key \"$LESSON_DATE_TO_KEY\" not found")
            } else {
                time = dateFormat.parse(temp) ?: Date(Long.MAX_VALUE).apply {
                    Log.w(TAG, "Suspicious behavior: " +
                            "Can not parse value of lesson date to key \"$LESSON_DATE_TO_KEY\"")
                }
            }
        }

        if (dateTo < dateFrom) {
            val buf = dateTo
            dateTo = dateFrom
            dateFrom = buf
        }

        val auditoriums = parseAuditoriums(json.array(LESSON_AUDITORIUMS_KEY))

        val type = json.string(LESSON_TYPE_KEY) ?: "".apply {
            Log.w(TAG, "Suspicious behavior: Lesson type key \"$LESSON_TYPE_KEY\" not found")
        }

        return Lesson(
            order, title, teachers,
            dateFrom, dateTo,
            auditoriums,
            type, group
        )
    }

    private fun parseTeachers(json: String?): List<Teacher> {
        if (json == null) return emptyList()

        return json.split(',').filter { it.isNotEmpty() }.map {
            Teacher(
                it.replace(" - ", "-")
                    .replace(" -", "-")
                    .replace("- ", "-")
                    .split(' ')
            )
        }
    }

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