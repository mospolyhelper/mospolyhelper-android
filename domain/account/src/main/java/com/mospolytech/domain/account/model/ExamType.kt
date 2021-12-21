package com.mospolytech.domain.perfomance.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExamType {
    Pass, Exam, MarkPass, CourseWork
}