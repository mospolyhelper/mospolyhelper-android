package com.mospolytech.mospolyhelper.domain.schedule.model.tag

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Parcelize
@Serializable
class LessonTagKey(val value: String) : Parcelable {
    companion object {
        fun fromLesson(lesson: Lesson, dayOfWeek: DayOfWeek, order: Int) =
            LessonTagKey(lesson.title + "/\\" + lesson.type  + "/\\" + dayOfWeek.value.toString()  + "/\\" +  order.toString())
    }
}