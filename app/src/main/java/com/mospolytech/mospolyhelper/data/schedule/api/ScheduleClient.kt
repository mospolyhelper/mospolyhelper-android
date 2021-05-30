package com.mospolytech.mospolyhelper.data.schedule.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*

class ScheduleClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://rasp.dmami.ru"

        private const val GET_SCHEDULE = "$BASE_URL/site/group"
        private const val GET_SCHEDULES_ALL = "$BASE_URL/semester.json"
        private const val GET_SCHEDULES_SESSION_ALL = "$BASE_URL/session.json"

        private const val BASE_URL_TEACHER = "https://kaf.dmami.ru"
        private const val GET_SCHEDULE_TEACHER = "$BASE_URL_TEACHER/lessons/teacher-html"
    }

    suspend fun getScheduleByGroup(groupTitle: String, isSession: Boolean): String {
        return client.get(GET_SCHEDULE) {
            header("referer", BASE_URL)
            // For json error status if schedule is not ready instead html
            header("X-Requested-With", "XMLHttpRequest")
            parameter("group", groupTitle)
            parameter("session", if (isSession) 1 else 0)
        }
    }

    suspend fun getScheduleByTeacher(teacherId: String): String {
        return client.get(GET_SCHEDULE_TEACHER) {
            header("referer", BASE_URL_TEACHER)
            header("X-Requested-With", "XMLHttpRequest")
            parameter("id", teacherId)
        }
    }

    suspend fun getSchedules(isSession: Boolean, onProgress: (Float) -> Unit = { }): String {
        return client.get<HttpStatement>(if (isSession) GET_SCHEDULES_SESSION_ALL else GET_SCHEDULES_ALL) {
            header("referer", BASE_URL)
            // For json error status if schedule is not ready instead html
            header("X-Requested-With", "XMLHttpRequest")
        }.execute { response ->
            val channel = response.receive<ByteReadChannel>()
            val contentLength = response.contentLength()?.toInt()
            requireNotNull(contentLength) { "Header needs to be set by server" }

            var total = 0
            var readBytes: Int
            val buffer = ByteArray(contentLength)
            do {
                readBytes = channel.readAvailable(buffer, total, 4096)
                total += readBytes
                onProgress(total.toFloat() / contentLength)
            } while (readBytes > 0)

            String(buffer)
        }
    }
}