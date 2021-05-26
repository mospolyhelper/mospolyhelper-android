package com.mospolytech.mospolyhelper.data.account.students.api

import io.ktor.client.*
import io.ktor.client.request.*

class StudentsHerokuClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://mospolyhelper.herokuapp.com"
        private const val ACCOUNT_MODULE = "/account"
        private const val GET_STUDENTS = "$BASE_URL$ACCOUNT_MODULE/portfolios"
    }

    suspend fun getStudents(searchQuery: String, page: Int): String {

        return client.get(GET_STUDENTS) {
            parameter("searchQuery", searchQuery)
            parameter("page", page)
        }
    }
}