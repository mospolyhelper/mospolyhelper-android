package com.mospolytech.mospolyhelper.domain.schedule.utils

import android.util.Log
import com.mospolytech.mospolyhelper.utils.TAG

enum class LessonTypes(
    val title: String,
    val isImportant: Boolean
) {
    CourseProject("Курсовой проект", true),
    Exam("Экзамен", true),
    Credit("Зачёт", true),
    CreditWithMark("Зачёт с оценкой", true),
    ExaminationReview("Экз. просмотр", true),
    ExaminationDepartmentalReview("Экз. каф. просмотр", true),
    Consultation("Консультация", false),
    LaboratoryWork("Лаб. работа", false),
    Practice("Практика", false),
    Lecture("Лекция", false),
    KeyLecture("Установочная лекция", false),
    LectureAndPracticeAndLaboratory("Лекц., практ., лаб.", false),
    LectureAndPractice("Лекц. и практ.", false),
    LectureAndLaboratory("Лекц. и лаб.", false),
    PracticeAndLaboratory("Практ. и лаб.", false),
    Other("Другое", false)
}
class LessonTypeParserPack(
    val sourceGroupType: String,
    val sourceTeacherType: String,
    val fixedType: LessonTypes
)

val lessonParserPacks = listOf(
    LessonTypeParserPack("КП", "КП", LessonTypes.CourseProject),
    LessonTypeParserPack("Экзамен", "Экз", LessonTypes.Exam),
    LessonTypeParserPack("Зачет", "Зач", LessonTypes.Credit),
    LessonTypeParserPack("ЗСО", "ЗСО", LessonTypes.CreditWithMark), // !!!
    LessonTypeParserPack("ЭП", "ЭП", LessonTypes.ExaminationReview), // !!!
    LessonTypeParserPack("ЭКП", "ЭКП", LessonTypes.ExaminationDepartmentalReview), // !!!
    LessonTypeParserPack("Консультация", "Кон", LessonTypes.Consultation),
    LessonTypeParserPack("Лаб. работа", "Лаб", LessonTypes.LaboratoryWork),
    LessonTypeParserPack("Практика", "Пра", LessonTypes.Practice),
    LessonTypeParserPack("Лекция", "Лек", LessonTypes.Lecture),
    LessonTypeParserPack("Установочная лекция", "Уст", LessonTypes.KeyLecture),
    LessonTypeParserPack("Лекция+практика", "лек.+пр.", LessonTypes.LectureAndPractice),
    LessonTypeParserPack("Другое", "Дру", LessonTypes.Other)
    )

object LessonTypeUtils {
    private const val PRACTICE_SHORT = "Пр"
    private const val LECTURE_SHORT = "Лек"
    private const val LABORATORY_SHORT = "Лаб"


    private val regex = Regex("\\(.*?\\)")

    fun fixType(type: String, lessonTitle: String): String {
        val fixedType = lessonParserPacks.firstOrNull { it.sourceGroupType.equals(type, true) }
        if (fixedType?.fixedType == LessonTypes.Other) {
            return fixOtherType(type, lessonTitle)
        }
        if (fixedType == null) {
            Log.d(TAG, type)
        }
        return fixedType?.fixedType?.title ?: type
    }


    fun fixTeacherType(type: String, lessonTitle: String): String {
        val fixedType = lessonParserPacks.firstOrNull { it.sourceTeacherType.equals(type, true) }
        if (fixedType?.fixedType == LessonTypes.Other) {
            return fixOtherType(type, lessonTitle)
        }
        return fixedType?.fixedType?.title ?: type
    }

    private fun fixOtherType(type: String, lessonTitle: String): String {
        val res = regex.findAll(lessonTitle).joinToString { it.value }
        return if (res.isNotEmpty()) {
            findCombinedShortTypeOrNull(res) ?: type
        } else {
            type
        }
    }

    private fun findCombinedShortTypeOrNull(type: String): String? {
        val lecture = type.contains(LECTURE_SHORT, true)
        val practise = type.contains(PRACTICE_SHORT, true)
        val lab = type.contains(LABORATORY_SHORT, true)
        return when {
            lecture && practise && lab -> LessonTypes.LectureAndPracticeAndLaboratory.title
            lecture && practise -> LessonTypes.LectureAndPractice.title
            lecture && lab -> LessonTypes.LectureAndLaboratory.title
            practise && lab -> LessonTypes.PracticeAndLaboratory.title
            lecture -> LessonTypes.Lecture.title
            practise -> LessonTypes.Practice.title
            lab -> LessonTypes.LaboratoryWork.title
            else -> null
        }
    }

    fun isTypeImportant(type: String): Boolean =
        LessonTypes.values()
            .firstOrNull { it.title.equals(type, true) }
            ?.isImportant ?: false
}