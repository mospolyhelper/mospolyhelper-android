package com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info

import com.mospolytech.mospolyhelper.data.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.data.schedule.repository.LessonLabelRepository
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import java.time.LocalDate

class LessonInfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    val lessonLabelRepository: LessonLabelRepository,
    private val deadlinesRepository: DeadlinesRepository
) : ViewModelBase(mediator, LessonInfoViewModel::class.java.simpleName) {
    companion object {
        const val LessonInfo = "LessonInfo"
    }
    var lesson: Lesson = Lesson.getEmpty(0)
    var date: LocalDate = LocalDate.now()

    init {
        subscribe(::handleMessage)
    }

    fun getSubjectDeadlines(subjectTitle: String) {
        deadlinesRepository.findItem(subjectTitle)
    }

    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            LessonInfo -> {
                lesson = message.content[0] as Lesson
                date = message.content[1] as LocalDate
            }
        }
    }

    fun openTeacherInfo(name: String) {

    }
}
