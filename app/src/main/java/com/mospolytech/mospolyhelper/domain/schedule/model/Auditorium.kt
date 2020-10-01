package com.mospolytech.mospolyhelper.domain.schedule.model

data class Auditorium(val title: String, val color: String) {
    companion object {
        fun parseEmoji(raw: String): String {
            return when {
                raw.contains("\uD83D\uDCF7") ->
                    raw.replace("\uD83D\uDCF7", "(Вебинар)")   // 📷
                raw.contains("\uD83C\uDFE0") ->
                    raw.replace("\uD83C\uDFE0", "(LMS)")  // 🏠
                else -> raw
            }
        }
    }
}