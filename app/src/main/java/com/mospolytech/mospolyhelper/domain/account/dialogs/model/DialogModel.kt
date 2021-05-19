package com.mospolytech.mospolyhelper.domain.account.dialogs.model

import com.mospolytech.mospolyhelper.utils.MessageDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class DialogModel(val id: Int,
                       val dialogKey: String,
                       val authorName: String,
                       val authorGroup: String,
                       val avatarUrl: String,
                       val message: String,
                       @Serializable(with = MessageDateSerializer::class)
                       val date: LocalDateTime,
                       val senderGroup: String,
                       val senderImageUrl: String,
                       val senderName: String,
                       val hasAttachments: Boolean,
                       val hasRead: Boolean
                       ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DialogModel

        if (id != other.id) return false
        if (dialogKey != other.dialogKey) return false
        if (authorName != other.authorName) return false
        if (authorGroup != other.authorGroup) return false
        if (avatarUrl != other.avatarUrl) return false
        if (message != other.message) return false
        if (date != other.date) return false
        if (senderImageUrl != other.senderImageUrl) return false
        if (senderName != other.senderName) return false
        if (hasAttachments != other.hasAttachments) return false
        if (hasRead != other.hasRead) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dialogKey.hashCode()
        result = 31 * result + authorName.hashCode()
        result = 31 * result + authorGroup.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + senderImageUrl.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + hasAttachments.hashCode()
        result = 31 * result + hasRead.hashCode()
        return result
    }
}
