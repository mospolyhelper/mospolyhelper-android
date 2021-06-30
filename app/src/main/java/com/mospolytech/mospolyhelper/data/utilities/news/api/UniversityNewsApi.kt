package com.mospolytech.mospolyhelper.data.utilities.news.api

import com.mospolytech.mospolyhelper.data.utilities.news.model.NewsResponse
import io.ktor.client.*
import io.ktor.client.request.*

class UniversityNewsApi(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://new.mospolytech.ru"

        private const val NEWS_ENDPOINT = "/news/"
        private const val EVENTS_ENDPOINT = "/events/"

        private const val NEWS_URL = "$BASE_URL$NEWS_ENDPOINT"
        private const val EVENTS_URL = "$BASE_URL$EVENTS_ENDPOINT"
    }

    suspend fun getNews(page: Int): NewsResponse {
        return client.get(NEWS_URL) {
            header("host", "new.mospolytech.ru")
            parameter("PAGEN_1", page)
        }
    }

    suspend fun getEvents(page: Int): NewsResponse {
        return client.get(EVENTS_URL) {
            parameter("PAGEN_1", page)
        }
    }
}