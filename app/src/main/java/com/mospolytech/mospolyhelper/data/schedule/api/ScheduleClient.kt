package com.mospolytech.mospolyhelper.data.schedule.api

import android.util.Log
import com.mospolytech.mospolyhelper.utils.TAG
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.ConstantCookiesStorage
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.Cookie

class ScheduleClient {
    companion object {
        private const val BASE_URL = "https://rasp.dmami.ru"
        private const val GET_SCHEDULE = "$BASE_URL/site/group"

        private const val BASE_URL_TEACHER = "https://kaf.dmami.ru"
        private const val GET_SCHEDULE_TEACHER = "https://kaf.dmami.ru/lessons/teacher-html"
    }
    private var cookiesStorage: CookiesStorage? = null
    private var cookiesCheckedCount = 0
        private set

    private val scheduleClient by lazy {
        HttpClient()
    }

    suspend fun getCookies() {
        val client = HttpClient()
        val data = client.get<String>(BASE_URL) {
            header("referer",
                BASE_URL
            )
            //header("host", BASE_URL)
            //header("X-Requested-With", "XMLHttpRequest")
        }
        Log.i(TAG, data)
        val regex = Regex("cookie=\".*?;")
        val matches = regex.findAll(data).iterator()
        if (!matches.hasNext()) {
            cookiesStorage = null
            cookiesCheckedCount += 1
            return
        }
        val cookie = matches.next().value
        val str = cookie
            .substring("cookie=\"".length, cookie.length - "cookie=\"".length - 1)
            .split('=')
        if (str.size < 2) {
            cookiesStorage = null
            cookiesCheckedCount += 1
            return
        }
        cookiesStorage = ConstantCookiesStorage(Cookie(str[0], str[1]))
        cookiesCheckedCount += 1
    }

    suspend fun getSchedule(groupTitle: String, isSession: Boolean): String {
        val cookiesStorage = cookiesStorage
        if (cookiesStorage != null) {
            scheduleClient.config {
                install(HttpCookies) {
                    storage = cookiesStorage
                }
            }
        }

        return scheduleClient.get(GET_SCHEDULE) {
            header("referer",
                BASE_URL
            )
            // Header below is for correct json error status when schedule is not ready but no html
            header("X-Requested-With", "XMLHttpRequest")
            parameter("group", groupTitle)
            parameter("session", if (isSession) 1 else 0)
        }
    }

    suspend fun getScheduleByTeacher(teacherId: String): String {

        return scheduleClient.get(GET_SCHEDULE_TEACHER) {
            header("referer",
                BASE_URL_TEACHER
            )
            // Header below is for correct json error status when schedule is not ready but no html
            header("X-Requested-With", "XMLHttpRequest")
            parameter("id", teacherId)
        }
    }
}