package com.mospolytech.mospolyhelper.domain.schedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Auditorium(val title: String, val color: String) {
    companion object {
        fun parseEmoji(raw: String): String {
            return when {
                raw.contains("\uD83D\uDCF7") ->
                    raw.replace("\uD83D\uDCF7", "(Вебинар)")   // 📷
                raw.contains("\uD83C\uDFE0") ->
                    raw.replace("\uD83C\uDFE0", "(LMS)")  // 🏠
                raw.contains("\uD83D\uDCBB") ->
                    raw.replace("\uD83D\uDCBB", "(Видеоконф.)") // 💻
                else -> raw
            }
        }
    }
}