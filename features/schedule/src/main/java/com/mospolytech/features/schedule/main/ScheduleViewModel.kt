package com.mospolytech.features.schedule.main

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.base.utils.getOrDefault
import com.mospolytech.domain.base.utils.isFinalFailure
import com.mospolytech.domain.base.utils.isNotLoading
import com.mospolytech.domain.schedule.model.schedule.ScheduleDay
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.schedule.model.WeekUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleState, ScheduleMutator, Nothing>(
    initState(),
    ::ScheduleMutator
) {

    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                if (!it.isFinalFailure) {
                    mutateState {
                        setSchedule(it.getOrDefault(emptyList()))
                        if (!state.isPreloading) {
                            setIsLoading(it.isLoading)
                        }
                    }
                }
            }
        }
    }

    fun onFabClick() {
        mutateState {
            setToday()
        }
    }

    fun onSchedulePosChanged(schedulePos: Int) {
        mutateState {
            setSchedulePos(schedulePos)
        }
    }

    fun onWeeksPosChanged(weeksPos: Int) {
        mutateState {
            setWeeksPos(weeksPos)
        }
    }
}

fun initState(): ScheduleState {
    return ScheduleMutator().apply {
        this.state = ScheduleState()
        setWeeksPos(state.selectedDate)
        setDayOfWeekPos(state.selectedDate)
    }.state
}

data class ScheduleState(
    val isPreloading: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val schedule: List<ScheduleDay> = emptyList(),
    val weeks: List<WeekUiModel> = emptyList(),

    val selectedDate: LocalDate = LocalDate.now(),
    val schedulePos: Int = 0,
    val weeksPos: Int = 0,
    val dayOfWeekPos: Int = 0,

    val showBackToTodayFab: Boolean = false
)

class ScheduleMutator : BaseMutator<ScheduleState>() {
    fun setSchedule(schedule: List<ScheduleDay>) =
        set(state.schedule, schedule) {
            copy(schedule = schedule)
        }.then {
            setWeeks(WeekUiModel.fromSchedule(schedule))
            setIsPreloading(false)
            setIsLoading(false)
            setSchedulePos(state.selectedDate)
            setWeeksPos(state.selectedDate)
            setDayOfWeekPos(state.selectedDate)
        }

    fun setWeeks(weeks: List<WeekUiModel>) =
        set(state.weeks, weeks) {
            copy(weeks = weeks)
        }

    fun setToday() {
        setSelectedDate(LocalDate.now())
    }

    fun setSelectedDate(selectedDate: LocalDate) =
        set(state.selectedDate, selectedDate) {
            copy(selectedDate = it)
        }.then {
            setSchedulePos(state.selectedDate)
            setWeeksPos(state.selectedDate)
            setDayOfWeekPos(state.selectedDate)
            setShowBackToTodayFabByDate(state.selectedDate)
        }

    fun setWeeksPos(date: LocalDate) {
        val pos = state.weeks.indexOfFirst { it.days.any { it.date == date } }.coerceAtLeast(0)
        setWeeksPos(pos)
    }


    fun setWeeksPos(weeksPos: Int) =
        set(state.weeksPos, weeksPos) {
            copy(weeksPos = it)
        }

    fun setDayOfWeekPos(date: LocalDate) {
        val pos = date.dayOfWeek.value - 1
        setDayOfWeekPos(pos)
    }


    fun setDayOfWeekPos(dayOfWeekPos: Int) =
        set(state.dayOfWeekPos, dayOfWeekPos) {
            copy(dayOfWeekPos = it)
        }

    fun setSchedulePos(date: LocalDate) {
        val pos = state.schedule.indexOfFirst { it.date == date }.coerceAtLeast(0)
        setSchedulePos(pos)
    }


    fun setSchedulePos(schedulePos: Int) =
        set(state.schedulePos, schedulePos) {
            copy(schedulePos = it)
        }.then {
            val date = state.schedule[schedulePos].date
            setSelectedDate(date)
        }

    fun setIsLoading(isLoading: Boolean) =
        set(state.isLoading, isLoading) {
            copy(isLoading = it)
        }.then {
            setIsError(!state.isLoading && state.isError)
        }

    fun setIsError(isError: Boolean) =
        set(state.isError, isError) {
            copy(isError = it)
        }

    fun setIsPreloading(isPreloading: Boolean) =
        set(state.isPreloading, isPreloading) {
            copy(isPreloading = it)
        }

    fun setShowBackToTodayFabByDate(date: LocalDate) {
        val dateIsToday = date == LocalDate.now()
        setShowBackToTodayFab(!dateIsToday)
    }

    fun setShowBackToTodayFab(showBackToTodayFab: Boolean) =
        set(state.showBackToTodayFab, showBackToTodayFab) {
            copy(showBackToTodayFab = it)
        }
}