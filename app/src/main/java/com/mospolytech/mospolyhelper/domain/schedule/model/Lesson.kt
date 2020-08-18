package com.mospolytech.mospolyhelper.domain.schedule.model

import android.util.Log
import com.beust.klaxon.Json
import com.mospolytech.mospolyhelper.utils.TAG
import java.time.LocalDate
import java.time.LocalTime

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
        private const val COURSE_PROJECT = "КП"
        private const val EXAM = "Экзамен"
        private const val CREDIT = "Зачет"
        private const val CREDIT_WITH_MARK = "ЗСО"
        private const val EXAMINATION_SHOW = "ЭП"
        private const val CONSULTATION = "Консультация"
        private const val LABORATORY = "Лаб"
        private const val PRACTICE = "Практика"
        private const val PRACTICE_SHORT = "Пр"
        private const val LECTURE = "Лекция"
        private const val OTHER = "Другое"
        // TODO Установочная лекция

        private const val COURSE_PROJECT_FIXED = "Курсовой проект"
        private const val CREDIT_WITH_MARK_FIXED = "Зачет с оценкой"
        private const val EXAMINATION_SHOW_FIXED = "Экз. показ"
        private const val LECTURE_PRACTICE_LABORATORY = "Лекц., практ., лаб."
        private const val LECTURE_PRACTICE = "Лекц. и практ."
        private const val LECTURE_LABORATORY = "Лекц. и лаб."
        private const val PRACTICE_LABORATORY = "Практ. и лаб."
        private val regex = Regex("\\(.*?\\)")

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

        fun getOrder(time: LocalTime, groupIsEvening: Boolean): Pair<Int, Boolean> =
            if (time > Time.thirdPair.second) when {
                time <= Time.fourthPair.second -> Pair(3, time >= Time.fourthPair.first)
                time <= Time.fifthPair.second -> Pair(4, time >= Time.fifthPair.first)
                groupIsEvening -> when {
                    time <= Time.sixthPairEvening.second -> Pair(5, time >= Time.sixthPairEvening.first)
                    time <= Time.seventhPairEvening.second -> Pair(6, time >= Time.seventhPairEvening.first)
                    else -> Pair(8, false)
                }
                else -> when {
                    time <= Time.sixthPair.second -> Pair(5, time >= Time.sixthPair.first)
                    time <= Time.seventhPair.second -> Pair(6, time >= Time.seventhPair.first)
                    else -> Pair(8, false)
                }
            }
            else when {
                time > Time.secondPair.second -> Pair(2, time >= Time.thirdPair.first)
                time > Time.firstPair.second -> Pair(1, time >= Time.secondPair.first)
                else -> Pair(0, time >= Time.firstPair.first)
            }

        fun getTime(order: Int, groupIsEvening: Boolean) = when (order) {
            0 -> Time.firstPairStr
            1 -> Time.secondPairStr
            2 -> Time.thirdPairStr
            3 -> Time.fourthPairStr
            4 -> Time.fifthPairStr
            5 -> if (groupIsEvening) Time.sixthPairEveningStr else Time.sixthPairStr
            6 -> if (groupIsEvening) Time.seventhPairEveningStr else Time.seventhPairStr
            else -> {
                Log.e(TAG, "Wrong order number of lesson")
                Pair("Ошибка", "номера занятия")
            }
        }

        fun getLocalTime(order: Int, groupIsEvening: Boolean) = when (order) {
            0 -> Time.firstPair
            1 -> Time.secondPair
            2 -> Time.thirdPair
            3 -> Time.fourthPair
            4 -> Time.fifthPair
            5 -> if (groupIsEvening) Time.sixthPairEvening else Time.sixthPair
            6 -> if (groupIsEvening) Time.seventhPairEvening else Time.seventhPair
            else -> {
                Log.e(TAG, "Wrong order number of lesson")
                Pair(LocalTime.MIN, LocalTime.MAX)
            }
        }


        fun fixType(type: String, subjectTitle: String): String {
            return when {
                type.equals(COURSE_PROJECT, true) -> COURSE_PROJECT_FIXED
                type.equals(CREDIT_WITH_MARK, true) -> CREDIT_WITH_MARK_FIXED
                type.equals(EXAMINATION_SHOW, true) -> EXAMINATION_SHOW_FIXED
                type.equals(OTHER, true) -> {
                    val res = regex.findAll(subjectTitle).joinToString { it.value }
                    if (res.isNotEmpty()) {
                        findCombinedShortTypeOrNull(res) ?: type
                    } else {
                        type
                    }
                }
                else -> type
            }
        }

        private fun findCombinedShortTypeOrNull(type: String): String? {
            val lecture = type.contains(LECTURE, true)
            val practise = type.contains(PRACTICE_SHORT, true)
            val lab = type.contains(LABORATORY, true)
            return when {
                lecture && practise && lab -> LECTURE_PRACTICE_LABORATORY
                lecture && practise -> LECTURE_PRACTICE
                lecture && lab -> LECTURE_LABORATORY
                practise && lab -> PRACTICE_LABORATORY
                lecture -> LECTURE
                practise -> PRACTICE
                lab -> LABORATORY
                else -> null
            }
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

            val seventhPair = Pair(
                LocalTime.of(19, 30),
                LocalTime.of(21, 0)
            )

            val seventhPairEvening = Pair(
                LocalTime.of(19, 50),
                LocalTime.of(21, 10)
            )

            val firstPairStr = "09:00" to "10:30"
            val secondPairStr = "10:40" to "12:10"
            val thirdPairStr = "12:20" to "13:50"
            val fourthPairStr = "14:30" to "16:00"
            val fifthPairStr = "16:10" to "17:40"
            val sixthPairStr = "17:50" to "19:20"
            val sixthPairEveningStr = "18:20" to "19:40"
            val seventhPairStr = "19:30" to "21:00"
            val seventhPairEveningStr = "19:50" to "21:10"
        }
    }

    val isEmpty = title.isEmpty() && type.isEmpty()
    val isNotEmpty = title.isNotEmpty() || type.isNotEmpty()

    @Json(ignored = true)
    val isImportant =
        type.contains(EXAM, true) ||
                type.contains(CREDIT, true) ||
                type.contains(COURSE_PROJECT_FIXED, true) ||
                type.contains(CREDIT_WITH_MARK_FIXED, true) ||
                type.contains(EXAMINATION_SHOW_FIXED, true)

    @Json(ignored = true)
    val time: Pair<String, String>
    get() {
        if (order == -1) {
            val q =1
            Log.d("1", "1")
        }
        return getTime(
            order,
            group.isEvening
        )
    }

    @Json(ignored = true)
    val localTime: Pair<LocalTime, LocalTime>
        get() {
            if (order == -1) {
                val q =1
                Log.d("1", "1")
            }
            return getLocalTime(
                order,
                group.isEvening
            )
        }


    fun equalsTime(lesson: Lesson) =
        order == lesson.order && group.isEvening == lesson.group.isEvening

    override fun compareTo(other: Lesson) = when {
        order != other.order -> order.compareTo(other.order)
        group.isEvening == other.group.isEvening -> group.title.compareTo(other.group.title)
        group.isEvening -> 1
        dateFrom != other.dateFrom -> dateFrom.compareTo(other.dateFrom)
        dateTo != other.dateTo -> dateTo.compareTo(other.dateTo)
        else -> -1
    }
}