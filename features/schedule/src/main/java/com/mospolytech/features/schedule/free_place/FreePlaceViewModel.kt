package com.mospolytech.features.schedule.free_place

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

class FreePlaceViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FreePlaceState())
    val state = _state.asStateFlow()
}

data class FreePlaceState(
    val q: String = ""
) : State<FreePlaceState.Mutator> {
    inner class Mutator : BaseMutator<FreePlaceState>(this) {
    }
    override fun mutator() = Mutator()
}