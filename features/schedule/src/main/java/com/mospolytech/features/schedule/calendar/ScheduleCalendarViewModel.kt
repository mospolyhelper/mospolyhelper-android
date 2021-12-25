package com.mospolytech.features.schedule.calendar

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleCalendarViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleCalendarState, ScheduleCalendarMutator>(
    ScheduleCalendarState(),
    ScheduleCalendarMutator()
){
    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                mutateState {
                    setSchedule(it.getOrDefault(emptyList()))
                }
            }
        }
    }
}

data class ScheduleCalendarState(
    val schedule: List<ScheduleDay> = emptyList()
)

class ScheduleCalendarMutator : BaseMutator<ScheduleCalendarState>() {
    fun setSchedule(schedule: List<ScheduleDay>) {
        if (state.schedule != schedule) {
            state = state.copy(schedule = schedule)
        }
    }
}