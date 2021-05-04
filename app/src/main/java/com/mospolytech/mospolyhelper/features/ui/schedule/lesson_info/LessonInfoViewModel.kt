package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonTagsRepositoryImpl
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.LessonPlace
import com.mospolytech.mospolyhelper.domain.schedule.repository.LessonTagsRepository
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import java.time.LocalDate

class LessonInfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    val tagRepository: LessonTagsRepository,
    private val deadlinesRepository: DeadlinesRepository
) : ViewModelBase(mediator, LessonInfoViewModel::class.java.simpleName) {
    companion object {
        const val LessonInfo = "LessonInfo"
    }
    var lessonPlace: LessonPlace = LessonPlace(emptyList(), 0, false)
    val lesson: Lesson
        get() = lessonPlace.lessons.firstOrNull() ?: Lesson.getEmpty()
    var date: LocalDate = LocalDate.now()

    fun getSubjectDeadlines(subjectTitle: String) {
        deadlinesRepository.findItem(subjectTitle)
    }

    fun openTeacherInfo(name: String) {

    }
}
