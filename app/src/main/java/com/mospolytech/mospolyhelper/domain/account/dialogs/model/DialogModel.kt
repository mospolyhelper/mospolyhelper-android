package com.mospolytech.mospolyhelper.domain.account.dialogs.model

data class DialogModel(val id: Int,
                       val dialogKey: String,
                       val authorName: String,
                       val authorGroup: String,
                       val avatarUrl: String,
                       val message: String,
                       val date: String,
                       val senderImageUrl: String,
                       val senderName: String,
                       val hasAttachments: Boolean,
                       val hasRead: Boolean
                       )
