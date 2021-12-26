package com.mospolytech.features.schedule.free_place

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.place.Place
import com.mospolytech.domain.schedule.model.source.ScheduleSources
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.onFailure
import com.mospolytech.features.base.utils.onSuccess
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class FreePlaceViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<FreePlaceState, FreePlaceMutator, Nothing>(
    FreePlaceState(),
    FreePlaceMutator()
) {
//    fun onDateRangeChange(dateRange: ClosedFloatingPointRange<Float>) {
//        mutateState {
//            setDateRange(dateRange)
//        }
//    }
//
//    fun onTimeRangeChange(dateRange: ClosedFloatingPointRange<Float>) {
//        mutateState {
//            setTimeRange(dateRange)
//        }
//    }

    init {
        viewModelScope.launch {
            useCase.getSources(ScheduleSources.Place)
                .onSuccess { mutateState { setPlaces(it.map { Place(it.key, it.title, it.description) }) } }
                .onFailure { mutateState { setPlaces(emptyList()) } }
                .collect()
        }
    }

    fun onDateSelect(date: LocalDate) {
        mutateState {
            setDate(date)
        }
    }

    fun onTimeFromSelect(timeFrom: LocalTime) {
        mutateState {
            setTimeFrom(timeFrom)
        }
    }

    fun onTimeToSelect(timeTo: LocalTime) {
        mutateState {
            setTimeTo(timeTo)
        }
    }

    fun onEnterFilterQuery(query: String) {
        mutateState {
            setFilterQuery(query)
        }
    }
}

data class FreePlaceState(
//    val dateFrom: LocalDate = LocalDate.now(),
//    val dateTo: LocalDate = LocalDate.now(),
//    val datesPositionRange: ClosedFloatingPointRange<Float> = 0f..1f,
//    val timeFrom: LocalTime = LocalTime.MIN,
//    val timeTo: LocalTime = LocalTime.MAX,
//    val timesPositionRange: ClosedFloatingPointRange<Float> = 0f..1f,
    val date: LocalDate = LocalDate.now(),
    val timeFrom: LocalTime = LocalTime.MIN,
    val timeTo: LocalTime = LocalTime.MAX,
    val filterQuery: String = "",
    val places: List<Place> = emptyList(),
    val filteredPlaces: List<Place> = emptyList(),
) {
    companion object {
        val minPerDay = LocalTime.MAX.toSecondOfDay() / 60
        val totalDays = 100L
    }
}

//private fun initState() : FreePlaceState {
//    val dateTo = LocalDate.now().plusDays(FreePlaceState.totalDays)
//    return FreePlaceState(dateTo = dateTo)
//}

class FreePlaceMutator : BaseMutator<FreePlaceState>() {
    fun setDate(date: LocalDate) =
        set(state.date, date) {
            copy(date = it)
        }
//    fun setDateFrom(dateFrom: LocalDate) =
//        set(state.dateFrom, dateFrom) {
//            copy(dateFrom = it)
//        }
//
//    fun setDateTo(dateTo: LocalDate) =
//        set(state.dateTo, dateTo) {
//            copy(dateTo = it)
//        }
//
//    fun setDateRange(datesPositionRange: ClosedFloatingPointRange<Float>) =
//        set(state.datesPositionRange, datesPositionRange) {
//            copy(datesPositionRange = it)
//        }.then {
//            setDateFrom(LocalDate.now().plusDays((FreePlaceState.totalDays * datesPositionRange.start).roundToLong()))
//            setDateTo(LocalDate.now().plusDays((FreePlaceState.totalDays * datesPositionRange.endInclusive).roundToLong()))
//        }

    fun setTimeFrom(timeFrom: LocalTime) =
        set(state.timeFrom, timeFrom) {
            copy(timeFrom = it)
        }

    fun setTimeTo(timeTo: LocalTime) =
        set(state.timeTo, timeTo) {
            copy(timeTo = it)
        }

    fun setFilterQuery(filterQuery: String) =
        set(state.filterQuery, filterQuery) {
            copy(filterQuery = it)
        }.then {
            setFilteredPlaces(state.places.filter { it.title.contains(state.filterQuery, ignoreCase = true) })
        }

    fun setPlaces(places: List<Place>) =
        set(state.places, places) {
            copy(places = it)
        }.then {
            setFilteredPlaces(state.places.filter { it.title.contains(state.filterQuery, ignoreCase = true) })
        }

    fun setFilteredPlaces(filteredPlaces: List<Place>) =
        set(state.filteredPlaces, filteredPlaces) {
            copy(filteredPlaces = it)
        }

//    fun setTimeRange(timesPositionRange: ClosedFloatingPointRange<Float>) =
//        set(state.timesPositionRange, timesPositionRange) {
//            copy(timesPositionRange = it)
//        }.then {
//            setTimeFrom(LocalTime.MIN.plusMinutes((FreePlaceState.minPerDay * timesPositionRange.start).roundToLong()))
//            setTimeTo(LocalTime.MIN.plusMinutes((FreePlaceState.minPerDay * timesPositionRange.endInclusive).roundToLong()))
//        }
}