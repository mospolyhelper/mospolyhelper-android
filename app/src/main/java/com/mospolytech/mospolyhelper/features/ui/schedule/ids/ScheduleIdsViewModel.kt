package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleIdsViewModel(
    private val scheduleUseCase: ScheduleUseCase
): ViewModelBase() {
    val users = scheduleUseCase.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val searchQuery = MutableStateFlow("")
    val filterMode = MutableStateFlow(FilterModes.All)

    suspend fun addSavedScheduleUser(user: UserSchedule) {
        scheduleUseCase.addSavedScheduleUser(user)
    }
}

enum class FilterModes {
    All,
    Groups,
    Teachers
}