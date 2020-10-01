package com.mospolytech.mospolyhelper.data.schedule.converter

import com.beust.klaxon.*
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleLocalConverter {
    companion object {
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
        val LESSON_GROUPS = Lesson::groups.name
        val TEACHER_NAMES = Teacher::names.name
        val AUDITORIUM_TITLE = Auditorium::title.name
        val AUDITORIUM_COLOR = Auditorium::color.name
    }

    private val localDateConverter = object : Converter {
        override fun canConvert(cls: Class<*>) =
            cls == LocalDate::class.java

        override fun fromJson(jv: JsonValue): Any? {
            return LocalDate.parse(jv.string!!, dateFormatter)
        }

        override fun toJson(value: Any) =
            "\"${(value as LocalDate).format(dateFormatter)}\""

    }

    val localDateTimeConverter = object : Converter {
        override fun canConvert(cls: Class<*>) =
            cls == LocalDateTime::class.java

        override fun fromJson(jv: JsonValue): Any? {
            return LocalDateTime.parse(jv.string!!, dateTimeFormatter)
        }

        override fun toJson(value: Any) =
            "\"${(value as LocalDateTime).format(dateTimeFormatter)}\""
    }

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun serializeSchedule(schedule: Schedule): String {
//        val converter = Klaxon().converter(localDateConverter)
//        return converter.toJsonString(schedule)

        val obj = json {
            obj(
                Schedule::dateFrom.name to schedule.dateFrom.format(dateFormatter),
                Schedule::dateTo.name to schedule.dateTo.format(dateFormatter),
                Schedule::dailySchedules.name to
                        array(
                            List(schedule.dailySchedules.size) { day ->
                                val dailySchedule = schedule.dailySchedules[day]
                                array(
                                    List(dailySchedule.size) { order ->
                                        val lesson = dailySchedule[order]
                                        obj(
                                            Lesson::order.name to lesson.order,
                                            Lesson::title.name to lesson.title,
                                            Lesson::type.name to lesson.type,
                                            Lesson::dateTo.name to lesson.dateTo.format(
                                                dateFormatter
                                            ),
                                            Lesson::dateFrom.name to lesson.dateFrom.format(
                                                dateFormatter
                                            ),
                                            Lesson::teachers.name to array(
                                                List(lesson.teachers.size) { i ->
                                                    val teacher = lesson.teachers[i]
                                                    obj(
                                                        Teacher::names.name to array(
                                                            List(teacher.names.size) { j ->
                                                                teacher.names[j]
                                                            }
                                                        )
                                                    )
                                                }
                                            ),
                                            Lesson::auditoriums.name to array(
                                                List(lesson.auditoriums.size) { i ->
                                                    val auditorium = lesson.auditoriums[i]
                                                    obj(
                                                        Auditorium::title.name to auditorium.title,
                                                        Auditorium::color.name to auditorium.color
                                                    )
                                                }
                                            ),
                                            Lesson::groups.name to array(
                                                List(lesson.groups.size) { i ->
                                                    val group = lesson.groups[i]
                                                    obj(
                                                        Group::title.name to group.title,
                                                        Group::isEvening.name to group.isEvening
                                                    )
                                                }
                                            )
                                        )
                                    }
                                )
                            }
                        )
            )
        }
        val q = obj.toJsonString()
        return obj.toJsonString()
    }

    fun deserializeSchedule(scheduleString: String): Schedule {
        val parser = Parser.default()
        val json = parser.parse(StringBuilder(scheduleString)) as JsonObject
        val dateFrom = LocalDate.parse(json.string(SCHEDULE_DATE_FROM)!!, dateFormatter)
        val dateTo = LocalDate.parse(json.string(SCHEDULE_DATE_TO)!!, dateFormatter)

        val dailySchedules = json
            .array<JsonArray<JsonObject>>(DAILY_SCHEDULES)!!
            .map { dailySchedule ->
                dailySchedule.map { lesson ->
                    Lesson(
                        lesson.int(LESSON_ORDER)!!,
                        lesson.string(LESSON_TITLE)!!,
                        lesson.array<JsonObject>(LESSON_TEACHERS)!!.map {
                            Teacher(
                                it.array(TEACHER_NAMES)!!
                            )
                        },
                        LocalDate.parse(lesson.string(LESSON_DATE_FROM)!!, dateFormatter),
                        LocalDate.parse(lesson.string(LESSON_DATE_TO)!!, dateFormatter),
                        lesson.array<JsonObject>(LESSON_AUDITORIUMS)!!.map {
                            Auditorium(
                                it.string(AUDITORIUM_TITLE)!!,
                                it.string(AUDITORIUM_COLOR)!!
                            )
                        },
                        lesson.string(LESSON_TYPE)!!,
                        lesson.array<JsonObject>(LESSON_GROUPS)!!.map {
                            Group(
                                it.string(Group::title.name)!!,
                                it.boolean(Group::isEvening.name)!!
                            )
                        }
                    )
                }
            }
        return Schedule(
            dailySchedules,
            dateFrom,
            dateTo
        )
    }
}