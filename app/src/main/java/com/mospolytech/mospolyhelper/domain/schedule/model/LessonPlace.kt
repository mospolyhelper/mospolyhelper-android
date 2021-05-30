package com.mospolytech.mospolyhelper.domain.schedule.model

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Parcelize
@Serializable
data class LessonPlace(
    val lessons: List<Lesson>,
    val time: LessonTime
) : ScheduleItem, Parcelable