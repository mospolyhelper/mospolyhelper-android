package com.mospolytech.mospolyhelper

import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleRemoteConverter
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
            ScheduleRemoteConverter()
        runBlocking {
            val q = client.getSchedule("181-721", false)
            val a = parser.parse(q, false)
            print(q)
        }
    }
}
