package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.core.model.Message
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagMessages
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class LessonTagViewModel(
    private val useCase: ScheduleUseCase
    ) : ViewModel() {

    val lesson = MutableStateFlow<Lesson?>(null)
    val dayOfWeek = MutableStateFlow<DayOfWeek?>(null)
    val order = MutableStateFlow<Int?>(null)

    val lessonTagKey = MutableStateFlow<LessonTagKey?>(null)
    val tag = MutableStateFlow<LessonTag?>(null)
    val title = MutableStateFlow("")
    val checkedColor = MutableStateFlow(LessonTagColors.ColorDefault)
    val tags = MutableStateFlow<List<LessonTag>>(emptyList())

    val message = MutableStateFlow<Message<LessonTagMessages>?>(null)

    init {
        viewModelScope.launch {
            combine(
                lesson,
                dayOfWeek,
                order
            ) { lesson, dayOfWeek, order ->
                if (lesson != null && dayOfWeek != null && order != null) {
                    lessonTagKey.value = LessonTagKey.fromLesson(lesson, dayOfWeek, order)
                } else {
                    lessonTagKey.value = null
                }
            }.collect()
        }

        viewModelScope.launch {
            useCase.getAllTags().collect {
                tags.value = it
            }
        }

        viewModelScope.launch {
            useCase.getTagsMessage().collect {
                message.value = it
            }
        }
    }



    suspend fun createTag() {
        lessonTagKey.value?.let {
            useCase.addTag(
                LessonTag(
                    title.value.take(15),
                    checkedColor.value.ordinal,
                    listOf(it)
                )
            )
        }
    }
}