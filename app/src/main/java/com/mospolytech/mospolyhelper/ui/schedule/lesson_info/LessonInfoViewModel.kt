package com.mospolytech.mospolyhelper.ui.schedule.lesson_info

import com.mospolytech.mospolyhelper.repository.models.schedule.Lesson
import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleViewModel
import java.util.*

class LessonInfoViewModel : ViewModelBase(Mediator(), LessonInfoViewModel::class.java.simpleName) {
    companion object {
        const val LessonInfo = "LessonInfo"
    }
    var lesson: Lesson = Lesson.getEmpty(0)
    var date: Calendar = Calendar.getInstance()

    init {
        subscribe(::handleMessage)
    }

    fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            LessonInfo -> {
                val list = message.content as List<*>
                lesson = list[0] as Lesson
                date = list[1] as Calendar
            }
        }
    }

    fun resaveSchedule() {
        send(ScheduleViewModel::class.java.simpleName, "ResaveSchedule", null)
    }
}
