package com.mospolytech.mospolyhelper.domain.account.statements.model

import kotlinx.serialization.Serializable

@Serializable
data class Statement(
    val id: String,
    val number: String,
    val subject: String,
    val sheetType: String,
    val loadType: String,
    val appraisalsDate: String,
    val grade: String,
    val courseAndSemester: String,
    var isExpanded: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (id != other.id) return false
        if (number != other.number) return false
        if (subject != other.subject) return false
        if (sheetType != other.sheetType) return false
        if (loadType != other.loadType) return false
        if (appraisalsDate != other.appraisalsDate) return false
        if (grade != other.grade) return false
        if (courseAndSemester != other.courseAndSemester) return false
        if (isExpanded != other.isExpanded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + sheetType.hashCode()
        result = 31 * result + loadType.hashCode()
        result = 31 * result + appraisalsDate.hashCode()
        result = 31 * result + grade.hashCode()
        result = 31 * result + courseAndSemester.hashCode()
        result = 31 * result + isExpanded.hashCode()
        return result
    }

}