package com.mospolytech.features.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.base.utils.WhileViewSubscribed
import com.mospolytech.domain.schedule.model.ScheduleSources
import com.mospolytech.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ScheduleViewModel(
    private val repository: ScheduleRepository
) : ViewModel() {
    val scheduleSources = repository.getSources(ScheduleSources.Group)
        .map { it.getOrDefault(emptyList()) }
        .stateIn(viewModelScope, WhileViewSubscribed, emptyList())
}