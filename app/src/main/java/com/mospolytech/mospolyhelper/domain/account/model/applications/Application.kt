package com.mospolytech.mospolyhelper.domain.account.model.applications

import kotlinx.serialization.Serializable

@Serializable
data class Application(
    val registrationNumber: String,
    val name: String,
    val dateTime: String,
    val status: String,
    val department: String,
    val note: String,
    val info: String,
    var isShown: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Application

        if (registrationNumber != other.registrationNumber) return false
        if (name != other.name) return false
        if (dateTime != other.dateTime) return false
        if (status != other.status) return false
        if (department != other.department) return false
        if (note != other.note) return false
        if (info != other.info) return false
        if (isShown != other.isShown) return false

        return true
    }

    override fun hashCode(): Int {
        var result = registrationNumber.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + department.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + isShown.hashCode()
        return result
    }

}