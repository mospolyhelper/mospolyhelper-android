package com.mospolytech.mospolyhelper.utils

import java.util.*
import java.util.concurrent.TimeUnit

class CalendarUtils {
    companion object {
        fun getDeltaInDays(first: Calendar, second: Calendar) =
            TimeUnit.DAYS.convert(
                first.time.time - second.time.time,
                TimeUnit.MILLISECONDS
            ).toInt()

        fun Calendar.addDays(days: Int) =
            (clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, days) }

        fun getMinValue(): Calendar =
            Calendar.getInstance().apply { time = Date(Long.MIN_VALUE) }

        fun getMaxValue(): Calendar =
            Calendar.getInstance().apply { time = Date(Long.MAX_VALUE) }
    }
}