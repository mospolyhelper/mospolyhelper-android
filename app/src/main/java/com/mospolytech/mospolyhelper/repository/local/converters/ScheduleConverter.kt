package com.mospolytech.mospolyhelper.repository.local.converters

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule

class ScheduleConverter {
    fun serialize(schedule: Schedule): String {
        val converter = Klaxon()
        return converter.toJsonString(schedule)
    }

    fun deserialize(scheduleString: String, fileName: String): Schedule {
        val converter = Klaxon()
        val parser = Parser.default()
        val json = parser.parse(scheduleString) as JsonObject

    }

    private fun prepare(parsedSchedule: Schedule, fileName: String): Schedule {
        for (dailySchedule in parsedSchedule.dailySchedules) {
            for (lesson in dailySchedule) {
                
            }
        }
    }
}