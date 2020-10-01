package com.mospolytech.mospolyhelper.features.ui.schedule.ids

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.ScheduleViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow

class ScheduleIdsViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val scheduleUseCase: ScheduleUseCase
): ViewModelBase(mediator, ScheduleIdsViewModel::class.java.simpleName) {
    val idSet = MutableStateFlow(emptySet<Pair<Boolean, String>>())
    val searchQuery = MutableStateFlow("")
    val filterMode = MutableStateFlow(FilterModes.All)

    init {
        getIdSet()
    }

    fun sendSelectedItem(item: Pair<Boolean, String>) {
        send(ScheduleViewModel::class.java.simpleName, ScheduleViewModel.MessageAddScheduleId, item)
    }

    private fun getIdSet() {
        viewModelScope.async {
            idSet.value = scheduleUseCase.getIdSet()
        }
    }
}

enum class FilterModes {
    All,
    Groups,
    Teachers
}