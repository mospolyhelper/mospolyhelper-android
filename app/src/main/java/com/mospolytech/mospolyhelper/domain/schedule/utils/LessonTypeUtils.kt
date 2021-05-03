package com.mospolytech.mospolyhelper.domain.schedule.utils

object LessonTypeUtils {
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

    fun typeImportant(type: String): Boolean {
        return type.contains(EXAM, true) ||
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
    }
}