package com.mospolytech.mospolyhelper.domain.schedule.utils

import com.mospolytech.mospolyhelper.domain.schedule.model.auditorium.Auditorium

val Auditorium.isOnline: Boolean
    get() =
        url.isNotEmpty() ||
                (AuditoriumTypes.values()
                    .firstOrNull { it.type == type }?.isOnline
                    ?: false)

enum class AuditoriumTypes(val type: String, val isOnline: Boolean) {
    Webinar("Вебинар", true),
    Lms("LMS", true),
    VideoConference("Видеоконф.", true),
    Other("", false)
}