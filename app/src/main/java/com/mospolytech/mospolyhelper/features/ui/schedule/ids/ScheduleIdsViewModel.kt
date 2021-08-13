package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ScheduleIdsViewModel(
    private val scheduleUseCase: ScheduleUseCase
): ViewModelBase() {
    val users = scheduleUseCase.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val searchQuery = MutableStateFlow("")
    val filterMode = MutableStateFlow(FilterModes.All)

    suspend fun addSavedScheduleUser(source: ScheduleSource) {
        scheduleUseCase.addSavedScheduleUser(source)
    }
}

enum class FilterModes {
    All,
    Groups,
    Teachers
}