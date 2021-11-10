package com.mospolytech.mospolyhelper.domain.account.model.statement

import com.mospolytech.mospolyhelper.domain.account.model.group_marks.Student
import com.mospolytech.mospolyhelper.domain.account.model.group_marks.Teacher
import kotlinx.serialization.Serializable

@Serializable
data class GradeSheet(
    val id: String,
    val guid: String,
    val documentType: String,
    val examType: String,
    val department: String,
    val school: String,
    val examDate: String,
    val examTime: String,
    val closeDate: String,
    val year: String,
    val course: String,
    val semester: String,
    val group: String,
    val disciplineName: String,
    val educationForm: String,
    val direction: String,
    val directionCode: String,
    val specialization: String,
    val teachers: List<Teacher>,
    val students: List<Student>,
    val fixed: Boolean,
    val modifiedDate: String
)
