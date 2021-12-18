package com.mospolytech.features.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.base.utils.WhileViewSubscribed
import com.mospolytech.domain.schedule.model.ScheduleDay
import com.mospolytech.domain.schedule.model.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.State
import com.mospolytech.features.base.mutate
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val useCase: ScheduleUseCase
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
}

data class ScheduleState(
    val schedule: List<ScheduleDay> = emptyList()
) : State<ScheduleState.Mutator> {
    inner class Mutator : BaseMutator<ScheduleState>(this) {
        fun setSchedule(schedule: List<ScheduleDay>) {
            if (state.schedule != schedule) {
                state = state.copy(schedule = schedule)
            }
        }
    }
    override fun mutator() = Mutator()
}