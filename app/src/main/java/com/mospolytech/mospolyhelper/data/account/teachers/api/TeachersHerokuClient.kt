package com.mospolytech.mospolyhelper.data.account.teachers.api

import io.ktor.client.*
import io.ktor.client.request.*

class TeachersHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_TEACHERS = "$BASE_URL$ACCOUNT_MODULE/teachers"
    }

    suspend fun getTeachers(searchQuery: String, page: Int, sessionId: String): String {

        return client.get(GET_TEACHERS) {
            parameter("searchQuery", searchQuery)
            parameter("page", page)
            header("sessionId", sessionId)
        }
    }
}