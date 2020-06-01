package com.mospolytech.mospolyhelper.ui.schedule.calendar

import com.mospolytech.mospolyhelper.repository.models.schedule.Schedule
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleViewModel
import java.util.*

class CalendarViewModel :
    ViewModelBase(Mediator(), CalendarViewModel::class.java.simpleName) {
    companion object {
        const val CalendarMode = "CalendarMode"
    }
    var schedule: Schedule? = null
    var scheduleFilter: Schedule.Filter = Schedule.Filter.default
    var date: Calendar = Calendar.getInstance()
    var isAdvancedSearch: Boolean = false

    init {
        subscribe(::handleMessage)
    }

    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            CalendarMode -> {
                val list = message.content as List<*>
                schedule = list[0] as Schedule
                scheduleFilter = list[1] as Schedule.Filter
                date = list[2] as Calendar
                isAdvancedSearch = list[3] as Boolean
            }
        }
    }

    fun dateChanged() {
        send(ScheduleViewModel::class.java.simpleName, "ChangeDate", date)
    }
}
