package com.mospolytech.features.base.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.format(pattern: String = "dd LLLL yyyy"): String = format(DateTimeFormatter.ofPattern(pattern))

fun LocalDateTime.format(pattern: String = "dd LLLL yyyy mm:hh"): String = format(DateTimeFormatter.ofPattern(pattern))