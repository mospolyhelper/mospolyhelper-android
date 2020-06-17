package com.mospolytech.mospolyhelper.repository.models.schedule

import android.util.Log
import com.beust.klaxon.Json
import com.mospolytech.mospolyhelper.TAG
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
        private const val COURSE_PROJECT = "кп"
        private const val EXAM = "экзамен"
        private const val CREDIT = "зачет"
        private const val CREDIT_WITH_MARK = "зсо"
        private const val EXAMINATION_SHOW = "эп"
        private const val CONSULTATION = "консультация"
        private const val LABORATORY = "лаб"
        private const val PRACTICE = "практика"
        private const val PRACTICE_SHORT = "пр"
        private const val LECTURE = "лекция"
        private const val LECTURE_SHORT = "лекция"
        private const val OTHER = "другое"

        private const val COURSE_PROJECT_FIXED = "курсовой проект"
        private const val EXAM_FIXED = "экзамен"
        private const val CREDIT_FIXED = "зачет"
        private const val CREDIT_WITH_MARK_FIXED = "зачёт с оценкой"
        private const val EXAMINATION_SHOW_FIXED = "экзаменационный показ"
        private const val CONSULTATION_FIXED = "консультация"
        private const val LABORATORY_FIXED = "лабораторная работа"
        private const val PRACTICE_FIXED = "практическое занятие"
        private const val LECTURE_FIXED = "лекционное занятие"
        private const val OTHER_FIXED = "другое занятие"
        private const val LECTURE_PRACTICE_LABORATORY = "лекц., прак. и лаб. работа"
        private const val LECTURE_PRACTICE = "лекция и практика"
        private const val LECTURE_LABORATORY = "лекц. и лаб. работа"
        private const val PRACTICE_LABORATORY = "практ. и лаб. работа"
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

        fun fixType(type: String, subjectTitle: String): String {
            return when {
                type.contains(COURSE_PROJECT, true) -> COURSE_PROJECT_FIXED
                type.contains(EXAM, true) -> EXAM_FIXED
                type.contains(CREDIT, true) -> CREDIT_FIXED
                type.contains(CREDIT_WITH_MARK, true) -> CREDIT_WITH_MARK_FIXED
                type.contains(EXAMINATION_SHOW, true) -> EXAMINATION_SHOW_FIXED
                type.contains(CONSULTATION, true) -> CONSULTATION_FIXED
                type.contains(OTHER, true) -> {
                    val res = regex.findAll(subjectTitle).joinToString { it.value }
                    if (res.isNotEmpty()) {
                        findCombinedShortTypeOrNull(res) ?: type
                    } else {
                        OTHER_FIXED
                    }
                }
                else -> findCombinedTypeOrNull(type) ?: type
            }
        }

        private fun findCombinedShortTypeOrNull(type: String): String? {
            val lecture = type.contains(LECTURE_SHORT, true)
            val practise = type.contains(PRACTICE_SHORT, true)
            val lab = type.contains(LABORATORY, true)
            return when {
                lecture && practise && lab -> LECTURE_PRACTICE_LABORATORY
                lecture && practise -> LECTURE_PRACTICE
                lecture && lab -> LECTURE_LABORATORY
                practise && lab -> PRACTICE_LABORATORY
                lecture -> LECTURE_FIXED
                practise -> PRACTICE_FIXED
                lab -> LABORATORY_FIXED
                else -> null
            }
        }

        private fun findCombinedTypeOrNull(type: String): String? {
            val lecture = type.contains(LECTURE, true)
            val practise = type.contains(PRACTICE, true)
            val lab = type.contains(LABORATORY, true)
            return when {
                lecture && practise && lab -> LECTURE_PRACTICE_LABORATORY
                lecture && practise -> LECTURE_PRACTICE
                lecture && lab -> LECTURE_LABORATORY
                practise && lab -> PRACTICE_LABORATORY
                lecture -> LECTURE_FIXED
                practise -> PRACTICE_FIXED
                lab -> LABORATORY_FIXED
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
                type.contains(CREDIT_FIXED, true) ||
                type.contains(COURSE_PROJECT_FIXED, true) ||
                type.contains(CREDIT_WITH_MARK_FIXED, true) ||
                type.contains(EXAMINATION_SHOW_FIXED, true)

    val time = getTime(order, group.isEvening)

    fun equalsTime(lesson: Lesson) =
        order == lesson.order && group.isEvening == lesson.group.isEvening

    override fun compareTo(other: Lesson) = when {
        order != other.order -> order.compareTo(other.order)
        group.isEvening == other.group.isEvening -> group.title.compareTo(other.group.title)
        group.isEvening -> 1
        else -> -1
    }
}