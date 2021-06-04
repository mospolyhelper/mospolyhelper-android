package com.mospolytech.mospolyhelper.domain.schedule.utils

import android.util.Log
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.utils.TAG
import java.time.LocalTime
import java.time.temporal.ChronoUnit

object LessonTimeUtils {
    enum class LessonTimes(
        val start: LocalTime,
        val end: LocalTime
    ) {
        Pair1(
            LocalTime.of(9, 0),
            LocalTime.of(10, 30)
        ),
        Pair2(
            LocalTime.of(10, 40),
            LocalTime.of(12, 10)
        ),
        Pair3(
            LocalTime.of(12, 20),
            LocalTime.of(13, 50)
        ),
        Pair4(
            LocalTime.of(14, 30),
            LocalTime.of(16, 0)
        ),
        Pair5(
            LocalTime.of(16, 10),
            LocalTime.of(17, 40)
        ),
        Pair6(
            LocalTime.of(17, 50),
            LocalTime.of(19, 20)
        ),
        Pair6Evening(
            LocalTime.of(18, 20),
            LocalTime.of(19, 40)
        ),
        Pair7(
            LocalTime.of(19, 30),
            LocalTime.of(21, 0)
        ),
        Pair7Evening(
            LocalTime.of(19, 50),
            LocalTime.of(21, 10)
        ),
        Undefined(LocalTime.MIN, LocalTime.MAX)
    }

    enum class LessonTimesStr(
        val start: String,
        val end: String
    ) {
        Pair1("9:00", "10:30"),
        Pair2("10:40", "12:10"),
        Pair3("12:20", "13:50"),
        Pair4("14:30", "16:00"),
        Pair5("16:10", "17:40"),
        Pair6("17:50", "19:20"),
        Pair6Evening("18:20", "19:40"),
        Pair7("19:30", "21:00"),
        Pair7Evening("19:50", "21:10"),
        Undefined("Ошибка", "номера занятия");

        operator fun component1() = start
        operator fun component2() = end
    }

    fun getCurrentTimes(time: LocalTime, lessonTimes: List<LessonTime>): List<LessonTime> {
        val sortedLessonTimes = lessonTimes.sorted()
        val resList = mutableListOf<LessonTime>()
        var closestTime = sortedLessonTimes.firstOrNull()

        for (lessonTime in sortedLessonTimes) {
            if (time in lessonTime) {
                resList.add(lessonTime)
            } else if (time <= lessonTime.localTime.end &&
                resList.isEmpty() &&
                closestTime != null
            ) {
                val timeSpan = time.until(lessonTime.localTime.end, ChronoUnit.SECONDS)
                val closestTimeSpan = time.until(closestTime.localTime.end, ChronoUnit.SECONDS)
                if (closestTimeSpan < 0 || timeSpan in 0 until closestTimeSpan) {
                    closestTime = lessonTime
                }
            }
        }
        if (resList.isEmpty() && closestTime != null) {
            val closestTimeSpan = time.until(closestTime.localTime.end, ChronoUnit.SECONDS)
            if (closestTimeSpan >= 0) {
                resList.add(closestTime)
            }
        }
        return resList
    }

    fun getTime(order: Int, groupIsEvening: Boolean) = when (order) {
        0 -> LessonTimesStr.Pair1
        1 -> LessonTimesStr.Pair2
        2 -> LessonTimesStr.Pair3
        3 -> LessonTimesStr.Pair4
        4 -> LessonTimesStr.Pair5
        5 -> if (groupIsEvening) LessonTimesStr.Pair6Evening else LessonTimesStr.Pair6
        6 -> if (groupIsEvening) LessonTimesStr.Pair7Evening else LessonTimesStr.Pair7
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            LessonTimesStr.Undefined
        }
    }

    fun getLocalTime(order: Int, groupIsEvening: Boolean) = when (order) {
        0 -> LessonTimes.Pair1
        1 -> LessonTimes.Pair2
        2 -> LessonTimes.Pair3
        3 -> LessonTimes.Pair4
        4 -> LessonTimes.Pair5
        5 -> if (groupIsEvening) LessonTimes.Pair6Evening else LessonTimes.Pair6
        6 -> if (groupIsEvening) LessonTimes.Pair7Evening else LessonTimes.Pair7
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            LessonTimes.Undefined
        }
    }
}