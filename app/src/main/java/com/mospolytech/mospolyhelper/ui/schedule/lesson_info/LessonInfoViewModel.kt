package com.mospolytech.mospolyhelper.ui.schedule.lesson_info

import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import java.time.LocalDate

class LessonInfoViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    val deadlinesRepository: DeadlinesRepository
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
