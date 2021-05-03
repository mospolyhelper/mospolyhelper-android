package com.mospolytech.mospolyhelper.data.schedule.api

import io.ktor.client.*
import io.ktor.client.request.*

class AuditoriumInfoApi(
    private val client: HttpClient
) {
    companion object {
        private const val URL = "https://github.com/mospolyhelper/up-to-date-information/blob/master/schedule-info/auditoriums.json"
    }

    suspend fun get(): String {
        return client.get(URL)
    }
}