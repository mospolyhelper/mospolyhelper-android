package com.mospolytech.mospolyhelper.domain.schedule.model

import android.util.Log
import com.mospolytech.mospolyhelper.utils.LocalDateSerializer
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Lesson(
    val order: Int,
    val title: String,
    val teachers: List<Teacher>,
    @Serializable(with = LocalDateSerializer::class)
    val dateFrom: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val dateTo: LocalDate,
    val auditoriums: List<Auditorium>,
    val type: String,
    val groups: List<Group>
) : Comparable<Lesson> {
    companion object {
        private const val COURSE_PROJECT_SHORT = "КП"
        private const val EXAM_SHORT = "Экз"
        private const val CREDIT_SHORT = "Зач"
        private const val CREDIT_WITH_MARK_SHORT = "ЗСО"
        private const val EXAMINATION_SHOW_SHORT = "ЭП"
        private const val CONSULTATION_SHORT = "Кон"
        private const val LABORATORY_SHORT = "Лаб"
        private const val PRACTICE_SHORT2 = "Пра"
        private const val LECTURE_SHORT = "Лек"
        private const val OTHER_SHORT = "Дру"

        private const val COURSE_PROJECT = "КП"
        private const val EXAM = "Экзамен"
        private const val CREDIT = "Зачет"
        private const val CREDIT_WITH_MARK = "ЗСО"
        private const val EXAMINATION_SHOW = "ЭП"
        private const val CONSULTATION = "Консультация"
        private const val LABORATORY = "Лаб"
        private const val LABORATORY_FULL = "Лаб. работа"
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
                listOf()
            )

        fun getOrder(time: LocalTime, groupIsEvening: Boolean): CurrentLesson =
            if (time > Time.thirdPair.second) when {
                time <= Time.fourthPair.second -> CurrentLesson(3, time >= Time.fourthPair.first, groupIsEvening)
                time <= Time.fifthPair.second -> CurrentLesson(4, time >= Time.fifthPair.first, groupIsEvening)
                groupIsEvening -> when {
                    time <= Time.sixthPairEvening.second -> CurrentLesson(5, time >= Time.sixthPairEvening.first, groupIsEvening)
                    time <= Time.seventhPairEvening.second -> CurrentLesson(6, time >= Time.seventhPairEvening.first, groupIsEvening)
                    else -> CurrentLesson(8, false, groupIsEvening)
                }
                else -> when {
                    time <= Time.sixthPair.second -> CurrentLesson(5, time >= Time.sixthPair.first, groupIsEvening)
                    time <= Time.seventhPair.second -> CurrentLesson(6, time >= Time.seventhPair.first, groupIsEvening)
                    else -> CurrentLesson(8, false, groupIsEvening)
                }
            }
            else when {
                time > Time.secondPair.second -> CurrentLesson(2, time >= Time.thirdPair.first, groupIsEvening)
                time > Time.firstPair.second -> CurrentLesson(1, time >= Time.secondPair.first, groupIsEvening)
                else -> CurrentLesson(0, time >= Time.firstPair.first, groupIsEvening)
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


        fun fixType(type: String, lessonTitle: String): String {
            return when {
                type.equals(COURSE_PROJECT, true) -> COURSE_PROJECT_FIXED
                type.equals(CREDIT_WITH_MARK, true) -> CREDIT_WITH_MARK_FIXED
                type.equals(EXAMINATION_SHOW, true) -> EXAMINATION_SHOW_FIXED
                type.equals(OTHER, true) -> {
                    val res = regex.findAll(lessonTitle).joinToString { it.value }
                    if (res.isNotEmpty()) {
                        findCombinedShortTypeOrNull(res) ?: type
                    } else {
                        type
                    }
                }
                else -> type
            }
        }


        fun fixTeacherType(type: String, lessonTitle: String): String {
            return when {
                type.equals(COURSE_PROJECT_SHORT, true) -> COURSE_PROJECT_FIXED
                type.equals(CREDIT_WITH_MARK_SHORT, true) -> CREDIT_WITH_MARK_FIXED
                type.equals(EXAMINATION_SHOW_SHORT, true) -> EXAMINATION_SHOW_FIXED
                type.equals(EXAM_SHORT, true) -> EXAM
                type.equals(CREDIT_SHORT , true) -> CREDIT
                type.equals(CONSULTATION_SHORT, true) -> CONSULTATION
                type.equals(LABORATORY_SHORT , true) -> LABORATORY_FULL
                type.equals(PRACTICE_SHORT2, true) -> PRACTICE
                type.equals(LECTURE_SHORT, true) -> LECTURE
                type.equals(OTHER_SHORT, true) -> {
                    val res = regex.findAll(lessonTitle).joinToString { it.value }
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
            val lecture = type.contains(LECTURE_SHORT, true)
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

            val firstPairStr = "9:00" to "10:30"
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

    @kotlinx.serialization.Transient
    val isEmpty = title.isEmpty() && type.isEmpty()
    @kotlinx.serialization.Transient
    val isNotEmpty = title.isNotEmpty() || type.isNotEmpty()

    @kotlinx.serialization.Transient
    val isImportant =
        type.contains(EXAM, true) ||
                type.contains(CREDIT, true) ||
                type.contains(COURSE_PROJECT_FIXED, true) ||
                type.contains(CREDIT_WITH_MARK_FIXED, true) ||
                type.contains(EXAMINATION_SHOW_FIXED, true) ||
                type.contains(COURSE_PROJECT, true) ||
                type.contains(CREDIT_WITH_MARK, true) ||
                type.contains(EXAMINATION_SHOW, true) ||
                EXAM.contains(type, true) ||
                CREDIT.contains(type, true) ||
                COURSE_PROJECT_FIXED.contains(type, true) ||
                CREDIT_WITH_MARK_FIXED.contains(type, true) ||
                EXAMINATION_SHOW_FIXED.contains(type, true) ||
                COURSE_PROJECT.contains(type, true) ||
                CREDIT_WITH_MARK.contains(type, true) ||
                EXAMINATION_SHOW.contains(type, true)

    val time: Pair<String, String>
    get() {
        return getTime(
            order,
            groupIsEvening
        )
    }

    val localTime: Pair<LocalTime, LocalTime>
        get() {
            return getLocalTime(
                order,
                groupIsEvening
            )
        }

    val groupIsEvening: Boolean
        get() = groups.firstOrNull()?.isEvening ?: false


    fun equalsTime(lesson: Lesson) =
        order == lesson.order && groupIsEvening == lesson.groupIsEvening

    override fun compareTo(other: Lesson): Int {
        if (order != other.order) return order.compareTo(other.order)
        if (groupIsEvening != other.groupIsEvening) return if (groupIsEvening) 1 else -1
        val g1 = groups.joinToString()
        val g2 = other.groups.joinToString()
        val groupComparing = g1.compareTo(g2)
        if (groupComparing != 0) return groupComparing
        if (dateFrom != other.dateFrom) return dateFrom.compareTo(other.dateFrom)
        return dateTo.compareTo(other.dateTo)
    }

    data class CurrentLesson(
        val order: Int,
        val isStarted: Boolean,
        val isEvening: Boolean
    ) {
        companion object {
            const val ORDER_LESSONS_FINISHED = 8
        }
        val isFinished = order == ORDER_LESSONS_FINISHED
        override fun equals(other: Any?) = false
        override fun hashCode(): Int {
            var result = order
            result = 31 * result + isStarted.hashCode()
            result = 31 * result + isEvening.hashCode()
            return result
        }
    }
}