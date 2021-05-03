package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class LessonTagViewModel(
    private val useCase: ScheduleUseCase
    ) : ViewModel() {

    val lesson = MutableStateFlow<Lesson?>(null)
    val tag = MutableStateFlow<LessonTag?>(null)
    val title = MutableStateFlow("")
    val color = MutableStateFlow(1)

    val checkedColor = MutableStateFlow(LessonTagColors.ColorDefault)


    fun saveTag() {
        lesson.value?.let {
        }
    }
}