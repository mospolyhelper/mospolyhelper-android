package com.mospolytech.mospolyhelper.features.ui.account.group_marks.other

data class MarksUi(
    val id: Int,
    val name: String,
    val mark: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MarksUi

        if (id != other.id) return false
        if (name != other.name) return false
        if (mark != other.mark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + mark.hashCode()
        return result
    }
}
