package com.mospolytech.features.schedule.sources

import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel

class ScheduleSourcesViewModel(
    private val useCase: ScheduleUseCase
): BaseViewModel<ScheduleSourceState, ScheduleSourceMutator, Nothing>(
    ScheduleSourceState(),
    ScheduleSourceMutator()
) {
    fun onClick() {
        mutateState {
            setSourceTypes(emptyList())
            setSourceTypes(emptyList())
        }
    }

}

data class ScheduleSourceState(
    val sourceTypes: List<ScheduleSources> = emptyList()
)

class ScheduleSourceMutator : BaseMutator<ScheduleSourceState>() {
    fun setSourceTypes(sourceTypes: List<ScheduleSources>) =
        set(state.sourceTypes, sourceTypes) {
            copy(sourceTypes = it)
        }
}