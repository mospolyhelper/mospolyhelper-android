package com.mospolytech.features.schedule.lesson_info

import com.mospolytech.domain.schedule.model.lesson.LessonInfo
import com.mospolytech.features.base.core.mvi.BaseViewModel

class LessonInfoViewModel : BaseViewModel<LessonInfoState>(LessonInfoState()) {
    fun onLessonInfo(lessonInfo: LessonInfo?) {
        mutateState {
            state = state.copy(lessonInfo = lessonInfo)
        }
    }
}

data class LessonInfoState(
    val lessonInfo: LessonInfo? = null
)