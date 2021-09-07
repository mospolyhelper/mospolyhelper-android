package com.mospolytech.mospolyhelper.features.ui.schedule.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import kotlinx.coroutines.flow.*

class CalendarViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    val scheduleSource = useCase.getSelectedScheduleSource()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val schedule = scheduleSource.filterNotNull().flatMapConcat {
        useCase.getSchedule(it)
    }.map { it.getOrNull() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}