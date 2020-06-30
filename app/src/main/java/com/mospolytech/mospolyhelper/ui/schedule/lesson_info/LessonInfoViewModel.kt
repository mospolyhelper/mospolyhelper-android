package com.mospolytech.mospolyhelper.ui.schedule.lesson_info

import android.content.Context
import com.mospolytech.mospolyhelper.repository.local.AppDatabase
import com.mospolytech.mospolyhelper.repository.deadline.DeadlinesRepository
import com.mospolytech.mospolyhelper.repository.schedule.models.Lesson
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleViewModel
import com.mospolytech.mospolyhelper.utils.ContextProvider
import com.mospolytech.mospolyhelper.utils.StaticDI
import java.time.LocalDate

class LessonInfoViewModel : ViewModelBase(StaticDI.viewModelMediator, LessonInfoViewModel::class.java.simpleName) {
    companion object {
        const val LessonInfo = "LessonInfo"
    }
    var lesson: Lesson = Lesson.getEmpty(0)
    var date: LocalDate = LocalDate.now()
    private val database: AppDatabase = AppDatabase.getDatabase(ContextProvider.context as Context)
    var deadlinesRepository =
        DeadlinesRepository(
            database
        )

    init {
        subscribe(::handleMessage)
    }

    fun getSubjectDeadlines(subjectTitle: String) {
        deadlinesRepository.findItem(subjectTitle)
    }

    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            LessonInfo -> {
                val list = message.content as List<*>
                lesson = list[0] as Lesson
                date = list[1] as LocalDate
            }
        }
    }
}
