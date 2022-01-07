package com.mospolytech.features.schedule.sources

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.onFailure
import com.mospolytech.features.base.utils.onSuccess
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ScheduleSourcesViewModel(
    private val useCase: ScheduleUseCase
): BaseViewModel<ScheduleSourceState, ScheduleSourceMutator, Nothing>(
    ScheduleSourceState(),
    ::ScheduleSourceMutator
) {
    init {
        viewModelScope.launch {
            useCase.getSourceTypes()
                .onSuccess { mutateState { setSourceTypes(it) } }
                .onFailure { mutateState { setSourceTypes(emptyList()) } }
                .collect()
        }
    }

    fun onSelectSourceType(sourceType: ScheduleSources) {
        mutateState {
            setSelectedSourceType(sourceType)
            state.selectedSourceType?.let {
                viewModelScope.launch {
                    useCase.getSources(it)
                        .onSuccess { mutateState { setSources(it) } }
                        .onFailure { mutateState { setSources(emptyList()) } }
                        .collect()
                }
            }

        }
    }

}

data class ScheduleSourceState(
    val sourceTypes: List<ScheduleSources> = emptyList(),
    val selectedSourceType: ScheduleSources? = null,
    val sources: List<ScheduleSourceFull> = emptyList()
)

class ScheduleSourceMutator : BaseMutator<ScheduleSourceState>() {
    fun setSourceTypes(sourceTypes: List<ScheduleSources>) =
        set(state.sourceTypes, sourceTypes) {
            copy(sourceTypes = it)
        }.then {
            if (state.selectedSourceType !in state.sourceTypes) {
                state.sourceTypes.firstOrNull()?.let {
                    setSelectedSourceType(it)
                }
            }
        }

    fun setSelectedSourceType(selectedSourceType: ScheduleSources?) =
        set(state.selectedSourceType, selectedSourceType) {
            copy(selectedSourceType = it)
        }

    fun setSources(sources: List<ScheduleSourceFull>) =
        set(state.sources, sources) {
            copy(sources = it)
        }
}