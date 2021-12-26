package com.mospolytech.domain.account.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExamType {
    Pass, Exam, MarkPass, CourseWork
}

fun ExamType.print(): String = when (this) {
    ExamType.Pass -> "Зачет"
    ExamType.Exam -> "Экзамен"
    ExamType.MarkPass -> "Диффиренцированный зачет"
    ExamType.CourseWork -> "Курсовая работа"
}