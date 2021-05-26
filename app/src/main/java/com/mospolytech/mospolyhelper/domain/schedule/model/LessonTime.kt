package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Parcelize
@Serializable
data class LessonTime(
    val order: Int,
    val isEvening: Boolean
) : Comparable<LessonTime>, Parcelable {
    val timeString: Pair<String, String>
        get() = LessonTimeUtils.getTime(order, isEvening)

    val localTime: Pair<LocalTime, LocalTime>
        get() = LessonTimeUtils.getLocalTime(order, isEvening)

    override fun compareTo(other: LessonTime): Int {
        if (order != other.order) return order.compareTo(other.order)
        return isEvening.compareTo(other.isEvening)
    }
}