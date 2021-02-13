package com.mospolytech.mospolyhelper.domain.schedule.model.tag

import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson

inline class LessonTagKey(val value: String) {
    companion object {
        fun fromLesson(lesson: Lesson) =
            LessonTagKey(lesson.title + "|||" + lesson.type)
    }
}