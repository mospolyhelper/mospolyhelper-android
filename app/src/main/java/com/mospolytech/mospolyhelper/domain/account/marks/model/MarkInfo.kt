package com.mospolytech.mospolyhelper.domain.account.marks.model

class MarkInfo(subject: String, loadType: String, mark: String,
               val id: Int, val semester: String, val course: String
               ): Mark(subject, loadType, mark) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MarkInfo

        if (id != other.id) return false
        if (semester != other.semester) return false
        if (course != other.course) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + semester.hashCode()
        result = 31 * result + course.hashCode()
        return result
    }

}