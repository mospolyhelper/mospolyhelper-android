package com.mospolytech.mospolyhelper

import com.mospolytech.mospolyhelper.data.schedule.api.ScheduleClient
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleLocalConverter
import com.mospolytech.mospolyhelper.data.schedule.converter.ScheduleTeacherRemoteConverter
import com.mospolytech.mospolyhelper.data.schedule.remote.ScheduleRemoteTeacherDataSource
import com.mospolytech.mospolyhelper.data.schedule.remote.TeacherListRemoteDataSource
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val client = ScheduleClient()
        val parser = ScheduleTeacherRemoteConverter()
        val converter = ScheduleLocalConverter()
        val teacherListDataSource = TeacherListRemoteDataSource()
        val ds = ScheduleRemoteTeacherDataSource(client, parser)
        val resFile = File("C:\\Users\\tipapro\\Desktop\\schedules")
        val teacherList = runBlocking { teacherListDataSource.get() ?: emptyMap() }

        val groups = listOf("171-721", "171-722", "171-723", "171-724", "171-725", "171-726",
            "181-721", "181-722", "181-723", "181-724", "181-725",
            "191-721", "191-722", "191-723", "191-724", "191-725", "191-726",
            "194-721", "194-722")

        val resList = mutableMapOf<String, List<MutableList<Lesson>>>()

        for (group in groups) {
            resList[group] = listOf(
                mutableListOf<Lesson>(), mutableListOf<Lesson>(), mutableListOf<Lesson>(),
                mutableListOf<Lesson>(), mutableListOf<Lesson>(), mutableListOf<Lesson>(), mutableListOf<Lesson>()
            )
        }


        runBlocking {
            for (teacher in teacherList.entries.withIndex()) {
                try {
                    println(teacher.index.toString() + " of " + teacherList.size)
                    val id = teacher.value.key
                    if (id == "-1") continue
                    val teacherName = teacher.value.value
                    val schedule = ds.get(id) ?: continue
                    for (key in resList.keys) {
                        for (dailySchedule in schedule.dailySchedules.withIndex()) {
                            (resList[key] ?: error(""))[dailySchedule.index]
                                .addAll(dailySchedule.value.filter { lesson -> lesson.groups.any { it.title == key } })
                        }
                    }
                } catch (e: Exception) {
                    println(e.toString())
                }
            }

            val resMap = mutableMapOf<String, String>()

            for (pair in resList) {
                val file = resFile.resolve("${pair.key}.json")
                if (file.exists()) {
                    file.delete()
                }
                file.createNewFile()
                file.writeText(
                    converter.serializeSchedule(
                        Schedule.from(pair.value.map { it.sorted() })
                    )
                )
            }

//            val resMap = mutableMapOf<String, Set<String>>()
//
//            for (pair in resList) {
//                val schedule = Schedule.from(pair.value.map { it.sorted() })
//                val teachers = ScheduleRepositoryImpl.allDataFromSchedule(schedule).lessonTeachers
//                resMap[pair.key] = teachers
//            }



        }

    }
}
