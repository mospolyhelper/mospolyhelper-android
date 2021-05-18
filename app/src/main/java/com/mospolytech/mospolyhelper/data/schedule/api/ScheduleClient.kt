package com.mospolytech.mospolyhelper.data.schedule.api

import android.util.Log
import com.mospolytech.mospolyhelper.BuildConfig
import com.mospolytech.mospolyhelper.utils.TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.features.cookies.ConstantCookiesStorage
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*

class ScheduleClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://rasp.dmami.ru"
        private const val GET_SCHEDULE = "$BASE_URL/site/group"

        private const val GET_SCHEDULES_ALL = "https://rasp.dmami.ru" + BuildConfig.URL_SCHEDULES_ALL
        private const val GET_SCHEDULES_SESSION_ALL = "https://rasp.dmami.ru" + BuildConfig.URL_SCHEDULES_SESSION_ALL

        private const val BASE_URL_TEACHER = "https://kaf.dmami.ru"
        private const val GET_SCHEDULE_TEACHER = "https://kaf.dmami.ru/lessons/teacher-html"
    }

    suspend fun getScheduleByGroup(groupTitle: String, isSession: Boolean): String {
        return client.get(GET_SCHEDULE) {
            header("referer", BASE_URL)
            // Header below is for correct json error status when schedule is not ready but no html
            header("X-Requested-With", "XMLHttpRequest")
            parameter("group", groupTitle)
            parameter("session", if (isSession) 1 else 0)
        }
    }

    suspend fun getSchedules(isSession: Boolean, onProgress: (Float) -> Unit = { }): String {
        return client.get<HttpStatement>(if (isSession) GET_SCHEDULES_SESSION_ALL else GET_SCHEDULES_ALL) {
            header("referer", BASE_URL)
            // Header below is for correct json error status when schedule is not ready but no html
            header("X-Requested-With", "XMLHttpRequest")
        }.execute { response ->
            val channel = response.receive<ByteReadChannel>()
            val contentLength = response.contentLength()?.toInt()
            requireNotNull(contentLength) {"Header needs to be set by server"}

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

    suspend fun getScheduleByTeacher(teacherId: String): String {
        return client.get(GET_SCHEDULE_TEACHER) {
            header("referer", BASE_URL_TEACHER)
            // Header below is for correct json error status when schedule is not ready but no html
            header("X-Requested-With", "XMLHttpRequest")
            parameter("id", teacherId)
        }
    }
}