package com.mospolytech.mospolyhelper.domain.account.model.students

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Int,
    val name: String,
    val avatarUrl: String,
    val group: String,
    val direction: String,
    val specialization: String,
    val course: String,
    val educationForm: String,
    var isExpanded: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatarUrl != other.avatarUrl) return false
        if (group != other.group) return false
        if (direction != other.direction) return false
        if (specialization != other.specialization) return false
        if (course != other.course) return false
        if (educationForm != other.educationForm) return false
        if (isExpanded != other.isExpanded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + group.hashCode()
        result = 31 * result + direction.hashCode()
        result = 31 * result + specialization.hashCode()
        result = 31 * result + course.hashCode()
        result = 31 * result + educationForm.hashCode()
        result = 31 * result + isExpanded.hashCode()
        return result
    }
}