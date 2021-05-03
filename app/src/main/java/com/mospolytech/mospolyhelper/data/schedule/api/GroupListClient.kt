package com.mospolytech.mospolyhelper.data.schedule.api

import android.util.Log
import com.mospolytech.mospolyhelper.utils.TAG
import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.ConstantCookiesStorage
import io.ktor.client.features.cookies.CookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.Cookie

class GroupListClient(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://rasp.dmami.ru"
        private const val GET_GROUP_LIST = "$BASE_URL/groups-list.json"
    }
    private var cookiesStorage: CookiesStorage? = null
    private var cookiesCheckedCount = 0
        private set

    suspend fun getCookies() {
        val client = HttpClient()
        val data = client.get<String>(BASE_URL) {
            header("referer",
                BASE_URL
            )
            //header("host", BASE_URL)
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

    suspend fun getGroupList(): String {
        val cookiesStorage = cookiesStorage
        if (cookiesStorage != null) {
            client.config {
                install(HttpCookies) {
                    storage = cookiesStorage
                }
            }
        }

        return client.get(GET_GROUP_LIST) {
            header("referer",
                BASE_URL
            )
            header("X-Requested-With", "XMLHttpRequest")
        }
    }
}