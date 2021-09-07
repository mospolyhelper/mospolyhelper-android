package com.mospolytech.mospolyhelper.features.ui.schedule.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScheduleSourcesViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {

    val favoriteScheduleSources = useCase.getFavoriteScheduleSources()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val selectedScheduleSource = useCase.getSelectedScheduleSource()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun selectScheduleSource(scheduleSource: ScheduleSource) {
        viewModelScope.launch {
            useCase.setSelectedScheduleSource(scheduleSource)
        }
    }

    fun removeUser(source: ScheduleSource) {
        viewModelScope.launch {
            useCase.removeFavoriteScheduleSource(source)
        }
    }
}