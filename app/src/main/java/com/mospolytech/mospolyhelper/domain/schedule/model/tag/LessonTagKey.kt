package com.mospolytech.mospolyhelper.domain.schedule.model.tag

import android.os.Parcelable
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

@Parcelize
@Serializable
data class LessonTagKey(
    val lessonTitle: String,
    val lessonType: String,
    val dayOfWeek: DayOfWeek,
    val lessonOrder: Int
    ) : Parcelable {
    companion object {
        fun fromLesson(lesson: Lesson, dayOfWeek: DayOfWeek, order: Int) =
            LessonTagKey(
                lesson.title,
                lesson.type,
                dayOfWeek,
                order
            )
    }
}