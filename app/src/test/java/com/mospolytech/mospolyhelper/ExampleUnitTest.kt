package com.mospolytech.mospolyhelper

import com.mospolytech.mospolyhelper.repository.schedule.ScheduleClient
import com.mospolytech.mospolyhelper.repository.schedule.ScheduleJsonParser
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val client =
            ScheduleClient()
        val parser =
            ScheduleJsonParser()
        runBlocking {
            val q = client.getSchedule("181-721", false)
            val a = parser.parse(q, false)
            print(q)
        }
    }
}
