package com.mospolytech.features.schedule.calendar

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.base.utils.getOrDefault
import com.mospolytech.domain.base.utils.isFinalFailure
import com.mospolytech.domain.base.utils.onSuccess
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.core.mvi.BaseMutator
import com.mospolytech.features.base.core.mvi.BaseViewModelFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleCalendarViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModelFull<ScheduleCalendarState, ScheduleCalendarMutator, Nothing>(
    ScheduleCalendarState(),
    ::ScheduleCalendarMutator
){
    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                if (!it.isFinalFailure) {
                    mutateState {
                        setSchedule(it.getOrDefault(emptyList()))
                    }
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