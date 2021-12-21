package com.mospolytech.domain.account.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExamType {
    Pass, Exam, MarkPass, CourseWork
}