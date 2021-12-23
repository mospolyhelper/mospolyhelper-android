package com.mospolytech.features.schedule.main

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.schedule.model.WeekUiModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleState, ScheduleMutator>(
    ScheduleState(),
    ScheduleMutator()
) {

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

data class ScheduleState(
    val schedule: List<ScheduleDay> = emptyList(),
    val weeks: List<WeekUiModel> = emptyList(),
    val scheduleDayPosition: Int = 0,
    val weeksPosition: Int = 0,
    val dayOfWeekPosition: Int = 0
)

class ScheduleMutator : BaseMutator<ScheduleState>() {
    fun setSchedule(schedule: List<ScheduleDay>) {
        if (state.schedule != schedule) {
            state = state.copy(schedule = schedule)
            setWeeks(WeekUiModel.fromSchedule(schedule))
        }
    }

    fun setWeeks(weeks: List<WeekUiModel>) {
        if (state.weeks != weeks) {
            state = state.copy(weeks = weeks)
        }
    }
}