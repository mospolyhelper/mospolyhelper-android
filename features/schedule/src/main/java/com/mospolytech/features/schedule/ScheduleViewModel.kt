package com.mospolytech.features.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.mospolytech.domain.base.utils.WeekIterator
import com.mospolytech.domain.base.utils.WhileViewSubscribed
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.model.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.State
import com.mospolytech.features.base.mutate
import com.mospolytech.features.base.navigation.ScheduleScreens
import com.mospolytech.features.schedule.model.WeekUiModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val useCase: ScheduleUseCase,
    private val navController: NavController
) : ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                _state.value = _state.value.mutate { setSchedule(it.getOrDefault(emptyList())) }
            }
        }
    }

    fun onCalendar() {
        navController.navigate(ScheduleScreens.Calendar.route)
    }
}

data class ScheduleState(
    val schedule: List<ScheduleDay> = emptyList(),
    val weeks: List<WeekUiModel> = emptyList(),
    val scheduleDayPosition: Int = 0,
    val weeksPosition: Int = 0,
    val dayOfWeekPosition: Int = 0
) : State<ScheduleState.Mutator> {
    inner class Mutator : BaseMutator<ScheduleState>(this) {
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
    override fun mutator() = Mutator()
}