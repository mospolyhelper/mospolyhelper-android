package com.mospolytech.mospolyhelper.domain.account.marks.model

data class Mark(
    val subject: String,
    val loadType: String,
    val mark: String
) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Mark
//
//        if (subject != other.subject) return false
//        if (loadType != other.loadType) return false
//        if (mark != other.mark) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = subject.hashCode()
//        result = 31 * result + loadType.hashCode()
//        result = 31 * result + mark.hashCode()
//        return result
//    }
}