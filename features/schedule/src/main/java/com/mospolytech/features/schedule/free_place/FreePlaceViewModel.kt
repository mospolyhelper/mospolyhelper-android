package com.mospolytech.features.schedule.free_place

import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.place.PlaceFilters
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import java.time.LocalDate

class FreePlaceViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<FreePlaceState, FreePlaceMutator>(
    FreePlaceState(),
    FreePlaceMutator()
) {

}

data class FreePlaceState(
    val dateFrom: LocalDate = LocalDate.MIN,
    val dateTo: LocalDate = LocalDate.MAX,
    val places: List<Place> = emptyList()
)

class FreePlaceMutator : BaseMutator<FreePlaceState>() {
    fun setDateFrom(dateFrom: LocalDate) =
        set(state.dateFrom, dateFrom) {
            copy(dateFrom = it)
        }

    fun setDateTo(dateTo: LocalDate) =
        set(state.dateTo, dateTo) {
            copy(dateTo = it)
        }
}