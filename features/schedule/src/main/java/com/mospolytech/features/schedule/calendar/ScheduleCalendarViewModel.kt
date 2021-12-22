package com.mospolytech.features.schedule.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.State
import com.mospolytech.features.base.mutate
import com.mospolytech.features.schedule.model.WeekUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleCalendarViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ScheduleCalendarState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                _state.value = _state.value.mutate { setSchedule(it.getOrDefault(emptyList())) }
            }
        }
    }
}

data class ScheduleCalendarState(
    val schedule: List<ScheduleDay> = emptyList()
) : State<ScheduleCalendarState.Mutator> {
    inner class Mutator : BaseMutator<ScheduleCalendarState>(this) {
        fun setSchedule(schedule: List<ScheduleDay>) {
            if (state.schedule != schedule) {
                state = state.copy(schedule = schedule)
            }
        }
    }
    override fun mutator() = Mutator()
}