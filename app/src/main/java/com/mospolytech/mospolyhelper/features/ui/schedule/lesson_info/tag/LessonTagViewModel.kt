package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek

class LessonTagViewModel(
    private val useCase: ScheduleUseCase
    ) : ViewModel() {

    private val _lesson = MutableStateFlow<Lesson?>(null)
    val lesson: StateFlow<Lesson?> = _lesson

    private val _dayOfWeek = MutableStateFlow<DayOfWeek?>(null)
    val dayOfWeek: StateFlow<DayOfWeek?> = _dayOfWeek

    private val _order = MutableStateFlow<Int?>(null)
    val order: StateFlow<Int?> = _order

    val lessonTagKey = combine(lesson, dayOfWeek, order) { lesson, dayOfWeek, order ->
        if (lesson != null && dayOfWeek != null && order != null) {
            LessonTagKey.fromLesson(lesson, dayOfWeek, order)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _checkedColor = MutableStateFlow(LessonTagColors.ColorDefault)
    val checkedColor: StateFlow<LessonTagColors> = _checkedColor

    val tags = useCase.getAllTags().transform {
        if (it is Result0.Failure) {
            _errorMessages.emit(it.exception)
        } else {
            emit(it)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val _errorMessages = MutableStateFlow<Throwable?>(null)
    val errorMessages: StateFlow<Throwable?> = _errorMessages


    fun setArgs(lesson: Lesson, dayOfWeek: DayOfWeek, order: Int) {
        _lesson.value = lesson
        _dayOfWeek.value = dayOfWeek
        _order.value = order
    }

    fun resetTagData() {
        _title.value = ""
        _checkedColor.value = LessonTagColors.ColorDefault
        _errorMessages.value = null
    }


    fun onColorChecked(color: LessonTagColors) {
        _checkedColor.value = color
    }

    fun onTitleChanged(title: String) {
        _title.value = title
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

    suspend fun lessonTagCheckedChanged(tag: LessonTag, lesson: LessonTagKey, isChecked: Boolean) {
        if (isChecked) {
            useCase.addTagToLesson(
                tag.title,
                lesson
            )
        } else {
            useCase.removeTagFromLesson(
                tag.title,
                lesson
            )
        }
    }


//    suspend fun editTag(tagTitle: String, newTitle: String, newColor: Int) {
//        useCase.editTag(tagTitle, newTitle, newColor)
//    }

    suspend fun removeTag(tagTitle: String) {
        useCase.removeTag(tagTitle)
    }

}