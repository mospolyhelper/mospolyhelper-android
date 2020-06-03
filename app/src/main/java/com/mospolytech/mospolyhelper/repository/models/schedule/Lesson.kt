package com.mospolytech.mospolyhelper.repository.models.schedule

import android.util.Log
import com.beust.klaxon.Json
import com.mospolytech.mospolyhelper.TAG
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class Lesson(
    val order: Int,
    val title: String,
    val teachers: List<Teacher>,
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val auditoriums: List<Auditorium>,
    val type: String,
    @Json(ignored = true) val group: Group
) : Comparable<Lesson> {
    companion object {
        const val COURSE_PROJECT = "кп"
        const val EXAM = "экзамен"
        const val CREDIT = "зачет"
        const val CREDIT_WITH_MARK = "зсо"
        const val EXAMINATION_SHOW = "эп"
        const val CONSULTATION = "консультация"
        const val LABORATORY = "лаб"
        const val PRACTICE = "практика"
        const val LECTURE = "лекция"
        const val OTHER = "другое"

        fun getEmpty(order: Int) =
            Lesson(
                order,
                "",
                emptyList(),
                LocalDate.MIN,
                LocalDate.MAX,
                emptyList(),
                "",
                Group.empty
            )

        fun getOrder(time: LocalTime, groupIsEvening: Boolean): Int =
            if (time > Time.thirdPair.second) when {
                time <= Time.fourthPair.second -> 3
                time <= Time.fifthPair.second -> 4
                groupIsEvening -> if (time <= Time.sixthPairEvening.second) 5 else 6
                else -> if (time <= Time.sixthPair.second) 5 else 6
            }
            else when {
                time > Time.secondPair.second -> 2
                time > Time.firstPair.second -> 1
                else -> 0
            }

    }

    private class Time {
        companion object {
            val firstPair = Pair(
                LocalTime.of(9, 0),
                LocalTime.of(10, 30)
            )
            val secondPair = Pair(
                LocalTime.of(10, 40),
                LocalTime.of(12, 10)
            )

            val thirdPair = Pair(
                LocalTime.of(12, 20),
                LocalTime.of(13, 50)
            )

            val fourthPair = Pair(
                LocalTime.of(14, 30),
                LocalTime.of(16, 0)
            )

            val fifthPair = Pair(
                LocalTime.of(16, 10),
                LocalTime.of(17, 40)
            )

            val sixthPair = Pair(
                LocalTime.of(17, 50),
                LocalTime.of(19, 20)
            )

            val sixthPairEvening = Pair(
                LocalTime.of(18, 20),
                LocalTime.of(19, 40)
            )

            val firstPairStr by lazy { "09:00" to "10:30" }
            val secondPairStr by lazy { "10:40" to "12:10" }
            val thirdPairStr by lazy { "12:20" to "13:50" }
            val fourthPairStr by lazy { "14:30" to "16:00" }
            val fifthPairStr by lazy { "16:10" to "17:40" }
            val sixthPairStr by lazy { "17:50" to "19:20" }
            val sixthPairEveningStr by lazy { "18:20" to "19:40" }
            val seventhPairStr by lazy { "19:30" to "21:00" }
            val seventhPairEveningStr by lazy { "19:50" to "21:10" }
        }
    }

    val isEmpty = title.isEmpty() && type.isEmpty()

    val isImportant =
        type.contains(EXAM, true) ||
                type.contains(CREDIT, true) ||
                type.contains(COURSE_PROJECT, true) ||
                type.contains(CREDIT_WITH_MARK, true) ||
                type.contains(EXAMINATION_SHOW, true)

    val time = when (order) {
        0 -> Time.firstPairStr
        1 -> Time.secondPairStr
        2 -> Time.thirdPairStr
        3 -> Time.fourthPairStr
        4 -> Time.fifthPairStr
        5 -> if (group.isEvening) Time.sixthPairEveningStr else Time.sixthPairStr
        6 -> if (group.isEvening) Time.seventhPairEveningStr else Time.seventhPairStr
        else -> {
            Log.e(TAG, "Wrong order number of lesson")
            Pair("Ошибка", "номера занятия")
        }
    }

    fun equalsTime(lesson: Lesson) =
        order == lesson.order && group.isEvening == lesson.group.isEvening

    override fun compareTo(other: Lesson) = when {
        order != other.order -> order.compareTo(other.order)
        group.isEvening == other.group.isEvening -> group.title.compareTo(other.group.title)
        group.isEvening -> 1
        else -> -1
    }
}