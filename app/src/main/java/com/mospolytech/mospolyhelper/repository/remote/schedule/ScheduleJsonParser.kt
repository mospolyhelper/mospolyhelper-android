package com.mospolytech.mospolyhelper.repository.remote.schedule

import android.util.Log
import com.beust.klaxon.*
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.model.schedule.Group
import com.mospolytech.mospolyhelper.repository.model.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.model.schedule.Schedule

class ScheduleJsonParser {
    companion object {
        // region Constants
        const val GROUP_LIST_KEY = "groups";

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

        const val LESSON_SUBJECT_KEY = "sbj";
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

        const val SESSION_DAYS_NUMBER = 20;
        const val WEEK_DAY_NUMBER = 7;
        // endregion
    }

    fun parse(scheduleString: String, isSessoon: Boolean): Schedule {
        val parser: Parser = Parser.default()
        val json = parser.parse(scheduleString) as JsonObject
        val status = json.string(STATUS_KEY)
        if (status == STATUS_ERROR) {
            val message = json.string(MESSAGE_KEY)
            throw JsonParsingException("Schedule was returned with error status. Message: \"${message ?: ""}\"")
        } else if (status != STATUS_OK) {
            val message = json.string(MESSAGE_KEY)
            Log.w(TAG, "Suspicious behavior: Schedule does not have status \"$STATUS_OK\" both \"$STATUS_ERROR\"." +
                    "Message: \"${message ?: ""}\"")
        }
        val isByDate = json.boolean(IS_SESSION) ?:
            throw JsonParsingException("Key \"$IS_SESSION\" not found")

        val group = parseGroup(json.obj(GROUP_KEY))
        val dailySchedules = parseDailySchedules(json.obj(SCHEDULE_GRID_KEY))

        return Schedule.Builder(
            dailySchedules = dailySchedules,
            group = group,
            isSession = isSessoon
        ).build()
    }

    private fun parseGroup(json: JsonObject?): Group = TODO()

    private fun parseDailySchedules(json: JsonObject?): List<List<Lesson>> = TODO()
}