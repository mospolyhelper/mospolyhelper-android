package com.mospolytech.mospolyhelper.repository.local.converters

import com.beust.klaxon.*
import com.mospolytech.mospolyhelper.repository.models.schedule.Group
import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class ScheduleConverter {
    companion object {
        val SCHEDULE_GROUP = Schedule::group.name
        val SCHEDULE_DATE_FROM = Schedule::dateFrom.name
        val SCHEDULE_DATE_TO = Schedule::dateTo.name
        val DAILY_SCHEDULES = Schedule::dailySchedules.name
        val LESSON_ORDER = Lesson::order.name
        val LESSON_TITLE = Lesson::title.name
        val LESSON_TEACHERS = Lesson::teachers.name
        val LESSON_DATE_FROM = Lesson::dateFrom.name
        val LESSON_DATE_TO = Lesson::dateTo.name
        val LESSON_AUDITORIUMS = Lesson::auditoriums.name
        val LESSON_TYPE = Lesson::type.name
    }

    val calendarConverter = object : Converter {
        override fun canConvert(cls: Class<*>) =
            cls == Calendar::class.java

        override fun fromJson(jv: JsonValue): Any? {
            val date = formatter.parse(jv.string!!)!!
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        }

        override fun toJson(value: Any) =
            formatter.format(value as Calendar)

    }

    private val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

    fun serializeSchedule(schedule: Schedule): String {
        val converter = Klaxon().converter(calendarConverter)
        return converter.toJsonString(schedule)
    }

    fun deserializeSchedule(scheduleString: String, isSession: Boolean, lastUpdate: Calendar): Schedule {
        val converter = Klaxon().converter(calendarConverter)
        val parser = Parser.default()
        val json = parser.parse(StringBuilder(scheduleString)) as JsonObject
        val group = converter.parseFromJsonObject<Group>(json.obj(SCHEDULE_GROUP)!!)!!
        val dateFrom = converter.parseFromJsonObject<Calendar>(json.obj(SCHEDULE_DATE_FROM)!!)!!
        val dateTo = converter.parseFromJsonObject<Calendar>(json.obj(SCHEDULE_DATE_TO)!!)!!

        val dailySchedules = json
            .array<JsonArray<JsonObject>>(DAILY_SCHEDULES)!!
            .map { dailySchedule ->
                dailySchedule.map { lesson ->
                    Lesson(
                        lesson.int(LESSON_ORDER)!!,
                        lesson.string(LESSON_TITLE)!!,
                        lesson.array(LESSON_TEACHERS)!!,
                        converter.parseFromJsonObject(lesson.obj(LESSON_DATE_FROM)!!)!!,
                        converter.parseFromJsonObject(lesson.obj(LESSON_DATE_TO)!!)!!,
                        lesson.array(LESSON_AUDITORIUMS)!!,
                        lesson.string(LESSON_TYPE)!!,
                        group
                    )
                }
            }
        return  Schedule(
            dailySchedules,
            lastUpdate,
            group,
            isSession,
            dateFrom,
            dateTo
        )
    }

    fun serializeGroupList(groupList: List<String>) =
        Klaxon().toJsonString(groupList)

    fun deserializeGroupList(groupListString: String) =
        Klaxon().parseArray<String>(groupListString)!!
}