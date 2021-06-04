package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTagKey
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result0
import com.mospolytech.mospolyhelper.utils.map
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class LessonInfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val tagRepository: LessonTagsRepository,
    private val deadlinesRepository: DeadlinesRepository
) : ViewModelBase(mediator, LessonInfoViewModel::class.java.simpleName) {

    var lessonTime: LessonTime = LessonTime(0, false)
    var lesson: Lesson = Lesson.getEmpty()
    var date: LocalDate = LocalDate.now()

    val tags = MutableStateFlow<Result0<List<LessonTag>>>(Result0.Failure(Exception()))

    fun setTags() {
        viewModelScope.launch {
            val tagKey = LessonTagKey.fromLesson(lesson, date.dayOfWeek, lessonTime.order)
            tagRepository.getAll()
                .map {
                    it.map {
                        it.filter { it.lessons.contains(tagKey) }
                    }
                }
                .collect {
                    tags.value = it
                }
        }
    }

    fun getSubjectDeadlines(subjectTitle: String) {
        deadlinesRepository.findItem(subjectTitle)
    }

    fun openTeacherInfo(name: String) {

    }
}
