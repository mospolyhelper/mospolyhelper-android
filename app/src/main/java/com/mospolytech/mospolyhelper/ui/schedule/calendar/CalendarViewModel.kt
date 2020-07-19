package com.mospolytech.mospolyhelper.ui.schedule.calendar

import com.mospolytech.mospolyhelper.repository.schedule.models.Schedule
import com.mospolytech.mospolyhelper.ui.common.Mediator
import com.mospolytech.mospolyhelper.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.ui.schedule.ScheduleViewModel
import java.time.LocalDate

class CalendarViewModel(
    mediator: Mediator<String, ViewModelMessage>
) : ViewModelBase(mediator, CalendarViewModel::class.java.simpleName) {
    companion object {
        const val CalendarMode = "CalendarMode"
    }
    var schedule: Schedule? = null
    var scheduleFilter: Schedule.Filter = Schedule.Filter.default
    var date: LocalDate = LocalDate.now()
    var isAdvancedSearch: Boolean = false

    init {
        subscribe(::handleMessage)
    }

    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            CalendarMode -> {
                schedule = message.content[0] as Schedule
                scheduleFilter = message.content[1] as Schedule.Filter
                date = message.content[2] as LocalDate
                isAdvancedSearch = message.content[3] as Boolean
            }
        }
    }

    fun dateChanged() {
        send(ScheduleViewModel::class.java.simpleName, "ChangeDate", date)
    }
}
