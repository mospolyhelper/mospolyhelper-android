package com.mospolytech.mospolyhelper.data.schedule.converter

import android.util.Log
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTypeUtils
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        private const val FIRST_MODULE_KEY = "fm"
        private const val SECOND_MODULE_KEY = "sm"
        private const val NO_MODULE_KEY = "no"

        private const val AUDITORIUM_TITLE_KEY = "title"
        private const val AUDITORIUM_COLOR_KEY = "color"

        private const val WEEK_DAY_NUMBER = 7

        // endregion

        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        private val regex1 = Regex("""(\p{L}|\))\(""")
        private val regex2 = Regex("""\)(\p{L}|\()""")
        private val regex3 = Regex("""(\p{L})-(\p{L})""")
        private val regex4 = Regex(""" -\S""")
        private val regex5 = Regex("""\S- """)
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
        val status = json.jsonObject[STATUS_KEY]?.jsonPrimitive?.content
        if (status == STATUS_ERROR) {
            val message = json.jsonObject[MESSAGE_KEY]?.jsonPrimitive?.content
            throw SerializationException(
                "Schedule was returned with error status. " +
                        "Message: \"${message ?: ""}\""
            )
        } else if (status != STATUS_OK) {
            val message = json.jsonObject[MESSAGE_KEY]?.jsonPrimitive?.content
//            Log.w(
//                TAG, "Schedule does not have status \"$STATUS_OK\" both \"$STATUS_ERROR\". " +
//                        "Message: \"${message ?: ""}\""
//            )
        }
        val isByDate =
            json.jsonObject[IS_SESSION]?.jsonPrimitive?.boolean
                ?: throw SerializationException("Key \"$IS_SESSION\" not found")

        val groupInfo = parseGroup(json.jsonObject[GROUP_KEY])
        val dailySchedules = parseDailySchedules(json.jsonObject[SCHEDULE_GRID_KEY], groupInfo.group, isByDate)

        return Schedule.from(dailySchedules)
    }

    private fun parseGroup(json: JsonElement?): GroupInfo {
        if (json == null) {
            Log.w(TAG, "GROUP_KEY \"$GROUP_KEY\" not found")
            return GroupInfo.empty
        }

        val title = json.jsonObject[GROUP_TITLE_KEY]?.jsonPrimitive?.content ?: "".apply {
            Log.w(TAG, "GROUP_TITLE_KEY \"$GROUP_TITLE_KEY\" not found")
        }

        val course = json.jsonObject[GROUP_COURSE_KEY]?.jsonPrimitive?.int ?: 0.apply {
            Log.w(TAG, "GROUP_COURSE_KEY \"$GROUP_COURSE_KEY\" not found")
        }

        val dateFrom = parseGroupDateFrom(json.jsonObject[GROUP_DATE_FROM_KEY]?.jsonPrimitive?.content)
        val dateTo = parseGroupDateTo(json.jsonObject[GROUP_DATE_TO_KEY]?.jsonPrimitive?.content)

        val isEvening = json.jsonObject[GROUP_EVENING_KEY]?.jsonPrimitive?.int ?: 0.apply {
            Log.w(TAG, "GROUP_EVENING_KEY \"$GROUP_EVENING_KEY\" not found")
        }

        val comment = json.jsonObject[GROUP_COMMENT_KEY]?.jsonPrimitive?.content ?: "".apply {
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

    private fun parseGroupDateFrom(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "GROUP_DATE_FROM_KEY \"$GROUP_DATE_FROM_KEY\" not found")
            LocalDate.MIN
        } else try {
            LocalDate.parse(json,
                dateFormatter
            )
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
            LocalDate.parse(json,
                dateFormatter
            )
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of GROUP_DATE_TO_KEY \"$GROUP_DATE_TO_KEY\"")
            LocalDate.MAX
        }
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
            // TODO: Is empty not suitable
            if (dailySchedule !is JsonObject || dailySchedule.isEmpty()) continue

            var parsedDay: Int
            var date = LocalDate.now()

            if (isByDate) {
                date = LocalDate.parse(day,
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
                tempList[parsedDay].add(LessonPlace(lessons, parsedOrder, group.isEvening))
            }
            tempList[parsedDay].sort()
        }
        return tempList
    }

    private fun parseLesson(
        json: JsonElement,
        group: Group,
        isByDate: Boolean,
        date: LocalDate
    ): Lesson {
        val title = processTitle(json.jsonObject[LESSON_TITLE_KEY]?.jsonPrimitive?.content
            ?: "Не найден ключ названия занятия. Возможно, структура расписания была обновлена: $json")

        val teachers = parseTeachers(json.jsonObject[LESSON_TEACHER_KEY]?.jsonPrimitive?.content)

        var dateFrom = if (isByDate) date else parseDateFrom(json.jsonObject[LESSON_DATE_FROM_KEY]?.jsonPrimitive?.content)
        var dateTo = if (isByDate) date else parseDateTo(json.jsonObject[LESSON_DATE_TO_KEY]?.jsonPrimitive?.content)

        if (dateTo < dateFrom) {
            val buf = dateTo
            dateTo = dateFrom
            dateFrom = buf
        }

        val auditoriums = parseAuditoriums(json.jsonObject[LESSON_AUDITORIUMS_KEY]?.jsonArray)

        val type = json.jsonObject[LESSON_TYPE_KEY]?.jsonPrimitive?.content ?: "".apply {
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

    private fun processTitle(rawTitle: String): String {
        return rawTitle
            .trim()
            .replace(regex1, "\$1 (")
            .replace(regex2, ") \$1")
            .replace(regex3, "\$1\u200b-\u200b\$2")
            .capitalize()
    }

    private fun parseDateFrom(json: String?): LocalDate {
        return if (json == null) {
            Log.w(TAG, "LESSON_DATE_FROM_KEY \"$LESSON_DATE_FROM_KEY\" not found")
            LocalDate.MIN
        } else try {
            LocalDate.parse(json,
                dateFormatter
            )
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
            LocalDate.parse(json,
                dateFormatter
            )
        } catch (e: Exception) {
            Log.w(TAG, "Can not parse value of LESSON_DATE_TO_KEY \"$LESSON_DATE_TO_KEY\"")
            LocalDate.MAX
        }
    }

    private fun parseTeachers(json: String?) = json?.run {
        trim()
            .capitalize()
            .split(',')
            .filter { it.isNotEmpty() }
            .map {
                Teacher(
                    it.replace(regex4, " - ")
                        .replace(regex5, " - ")
                        .replace("  ", " ")
                )
            }
    } ?: emptyList()


    private fun parseAuditoriums(json: JsonArray?): List<Auditorium> {
        if (json == null || json.isEmpty()) return listOf()

        val tempList = mutableListOf<Auditorium>()
        for (auditorium in json) {
            var name = auditorium.jsonObject[AUDITORIUM_TITLE_KEY]?.jsonPrimitive?.content?.trim()?.capitalize() ?: continue
            name = Auditorium.parseEmoji(name)

            val color = auditorium.jsonObject[AUDITORIUM_COLOR_KEY]?.jsonPrimitive?.content ?: ""
            tempList.add((Auditorium(
                name,
                color
            )))
        }
        return tempList
    }
}