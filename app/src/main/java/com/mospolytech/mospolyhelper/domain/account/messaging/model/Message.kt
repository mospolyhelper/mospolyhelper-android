package com.mospolytech.mospolyhelper.domain.account.messaging.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int,
    val avatarUrl: String,
    val authorName: String,
    val message: String,
    val attachments: List<Attachment>,
    val removeUrl: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false
        if (avatarUrl != other.avatarUrl) return false
        if (authorName != other.authorName) return false
        if (message != other.message) return false
        if (attachments != other.attachments) return false
        if (removeUrl != other.removeUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + attachments.hashCode()
        result = 31 * result + removeUrl.hashCode()
        return result
    }
}