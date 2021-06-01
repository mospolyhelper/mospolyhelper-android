package com.mospolytech.mospolyhelper.utils

import java.time.*

fun ZonedDateTime.toMoscow(): ZonedDateTime {
    return withZoneSameInstant(ZoneOffset.ofHours(3))
}

fun moscowZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.now(ZoneOffset.ofHours(3))
}

fun moscowLocalDateTime(): LocalDateTime {
    return LocalDateTime.now(ZoneOffset.ofHours(3))
}

fun moscowLocalDate(): LocalDate {
    return LocalDate.now(ZoneOffset.ofHours(3))
}

fun moscowLocalTime(): LocalTime {
    return LocalTime.now(ZoneOffset.ofHours(3))
}