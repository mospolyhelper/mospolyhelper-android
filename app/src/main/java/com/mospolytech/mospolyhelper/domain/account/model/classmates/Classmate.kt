package com.mospolytech.mospolyhelper.domain.account.model.classmates

import kotlinx.serialization.Serializable

@Serializable
data class Classmate (
    val id: Int,
    val name: String,
    val avatarUrl: String,
    val status: String,
    val dialogKey: String
        ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Classmate

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatarUrl != other.avatarUrl) return false
        if (status != other.status) return false
        if (dialogKey != other.dialogKey) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + dialogKey.hashCode()
        return result
    }
}