package com.mospolytech.mospolyhelper.data.schedule.api

import io.ktor.client.*
import io.ktor.client.request.*

class TeacherInfoApi(
    private val client: HttpClient
) {
    companion object {
        private const val URL = "https://raw.githubusercontent.com/mospolyhelper/up-to-date-information/master/schedule-info/groups.json"
    }

    suspend fun get(): String {
        return client.get(URL)
    }
}