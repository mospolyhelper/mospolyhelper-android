package com.mospolytech.features.schedule.free_place

import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel

class FreePlaceViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<FreePlaceState, FreePlaceMutator, Nothing>(
    FreePlaceState(),
    FreePlaceMutator()
) {

}

data class FreePlaceState(
    val q: String = ""
)

class FreePlaceMutator : BaseMutator<FreePlaceState>() {
}