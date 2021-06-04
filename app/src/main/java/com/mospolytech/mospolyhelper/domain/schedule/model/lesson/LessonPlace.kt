package com.mospolytech.mospolyhelper.domain.schedule.model.lesson

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LessonPlace(
    val lessons: List<Lesson>,
    val time: LessonTime
) : ScheduleItem, Parcelable