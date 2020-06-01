package com.mospolytech.mospolyhelper.repository.models.schedule

import android.util.Log
import com.beust.klaxon.Json
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.utils.CalendarUtils
import java.util.*

data class Lesson(
    val order: Int,
    val title: String,
    val teachers: List<Teacher>,
    val dateFrom: Calendar,
    val dateTo: Calendar,
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
                CalendarUtils.getMinValue(),
                CalendarUtils.getMaxValue(),
                emptyList(),
                "",
                Group.empty
            )

        fun getOrder(time: Calendar, groupIsEvening: Boolean): Int =
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
            val firstPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 9)
                    set(Calendar.MINUTE, 0)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 10)
                    set(Calendar.MINUTE, 30)
                }
            )
            val secondPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 10)
                    set(Calendar.MINUTE, 40)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 12)
                    set(Calendar.MINUTE, 10)
                }
            )
            val thirdPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 12)
                    set(Calendar.MINUTE, 20)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 13)
                    set(Calendar.MINUTE, 50)
                }
            )

            val fourthPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 14)
                    set(Calendar.MINUTE, 30)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 16)
                    set(Calendar.MINUTE, 0)
                }
            )
            val fifthPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 16)
                    set(Calendar.MINUTE, 10)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 17)
                    set(Calendar.MINUTE, 40)
                }
            )
            val sixthPair
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 17)
                    set(Calendar.MINUTE, 50)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 19)
                    set(Calendar.MINUTE, 20)
                }
            )
            val sixthPairEvening
                get() = Pair(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 18)
                    set(Calendar.MINUTE, 20)
                },
                Calendar.getInstance().apply {
                    set(Calendar.HOUR, 19)
                    set(Calendar.MINUTE, 40)
                }
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