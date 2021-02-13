package com.mospolytech.mospolyhelper.features.ui.account.students.other

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterEntity(val courses: List<String>, val form: List<String>, val type: List<String>): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterEntity

        if (courses != other.courses) return false
        if (form != other.form) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = courses.hashCode()
        result = 31 * result + form.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}