package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Parcelize
@Serializable
data class LessonPlace(
    val lessons: List<Lesson>,
    val order: Int,
    val isEvening: Boolean
) : Comparable<LessonPlace>, ScheduleItem, Parcelable {
    val time: Pair<String, String>
        get() = LessonTimeUtils.getTime(order, isEvening)

    val localTime: Pair<LocalTime, LocalTime>
        get() = LessonTimeUtils.getLocalTime(order, isEvening)

    fun equalsTime(lessonPlace: LessonPlace) =
        order == lessonPlace.order && isEvening == lessonPlace.isEvening

    override fun compareTo(other: LessonPlace): Int {
        if (order != other.order) return order.compareTo(other.order)
        return isEvening.compareTo(other.isEvening)
    }
}