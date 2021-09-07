package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.features.ui.schedule.model.*
import com.mospolytech.mospolyhelper.utils.*
import com.tipapro.mvilight.coroutines.boundWith
import com.tipapro.mvilight.coroutines.scope
import com.tipapro.mvilight.main.Store
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class ScheduleViewModel(
    useCase: ScheduleUseCase
) : ViewModel() {
    val store = ScheduleStore(useCase).boundWith(viewModelScope)
}


//    private fun removeFilterType(filterType: String) {
//        state = state.copy(filterTypes = state.filterTypes - filterType)
//    }
//
//    private fun addFilterType(filterType: String) {
//        state = state.copy(filterTypes = state.filterTypes + filterType)
//    }
//
//
//    private fun setFilteredSchedule(filteredSchedule: Schedule?) {
//        if (state.filteredSchedule != filteredSchedule) {
//            val scheduleDatesUiData = ScheduleDatesUiData(filteredSchedule)
//            val newDates = filteredSchedule?.let { it.dateFrom..it.dateTo } ?: LocalDate.now()..LocalDate.now()
//            val newDatesWeek = scheduleDatesUiData.dates
//            val newDate = getBoundedDate(state.date, newDates)
//            val newSchedulePosition = newDates.start.until(newDate, ChronoUnit.DAYS).toInt()
//            state = state.copy(
//                filteredSchedule = filteredSchedule,
//                scheduleDatesUiData = scheduleDatesUiData,
//                dates = newDates,
//                date = newDate,
//                schedulePosition = newSchedulePosition,
//                datesWeek = newDatesWeek
//            )
//        }
//    }
//
//
//    private fun setLessonDateFilter(lessonDateFilter: LessonDateFilter) {
//        if (state.lessonDateFilter != lessonDateFilter) {
//            state = state.copy(lessonDateFilter = lessonDateFilter)
//        }
//    }
//
//    private fun setSchedule(schedule: Schedule?) {
//        if (state.schedule != schedule) {
//            val newAllLessonTypes = schedule?.getAllTypes() ?: emptySet()
//            state = state.copy(schedule = schedule, allLessonTypes = newAllLessonTypes)
//        }
//    }


class ScheduleStore(
    private val useCase: ScheduleUseCase
) : Store<ScheduleState, ScheduleIntent, Nothing>(ScheduleState.init()) {

    init {
        scope.launch {
            useCase.getSelectedScheduleSource().collect {
                sendIntent(ScheduleIntent.SelectScheduleSource(it))
            }
        }

        sendIntent(ScheduleIntent.SetToday)

        scope.launch {
            while (isActive) {
                sendIntent(ScheduleIntent.SetCurrentTimes)
                delay((60L - LocalTime.now().second) * 1000L)
            }
        }
    }

    override fun ResultState.processIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.SelectScheduleSource -> selectScheduleSource(intent.scheduleSource)
            ScheduleIntent.SetToday -> setToday()
            ScheduleIntent.RefreshSchedule -> refreshSchedule()
            is ScheduleIntent.SetDate -> setDate(intent.date)
            is ScheduleIntent.SetSchedulePosition -> setSchedulePosition(intent.position)
            is ScheduleIntent.SetAdvancedSearch -> setAdvancedSearch(intent.scheduleFilters)
            ScheduleIntent.SetCurrentTimes -> setCurrentTimes()
            is ScheduleIntent.SetSchedule -> setSchedule2(intent.schedule, intent.rawSchedule)
            is ScheduleIntent.SetError -> setError(intent.error)
        }
        commitState()
    }

    private fun ResultState.setAdvancedSearch(scheduleFilters: ScheduleFilters?) {
        scheduleFilters?.let {
            selectScheduleSource(AdvancedSearchScheduleSource(scheduleFilters))
        }

    }

    private fun ResultState.selectScheduleSource(scheduleSource: ScheduleSource?) {
        if (state.scheduleSource != scheduleSource) {
            state = state.copy(scheduleSource = scheduleSource)
            setSchedule(scheduleSource)
        } else {
            if (state.isLoading) {
                setError(ScheduleError.GroupNotSelected)
                setIsLoading(false)
            }
        }
    }

    private fun ResultState.refreshSchedule() {
        setIsRefreshing(true)
        state.scheduleSource?.let {
            setSchedule(it)
        }
    }

    private fun ResultState.setCurrentTimes() {
        if (state.schedule.isNotEmpty()) {
            val position = state.dateFrom.until(moscowLocalDate(), ChronoUnit.DAYS).toInt()
            val lessons = state.schedule[position]
            val lessonTimes = lessons.lessons.filterIsInstance<LessonTimePack>().map { it.time }
            val time = moscowLocalTime()
            state = state.copy(currentLessonTimes = Pair(LessonTimeUtils.getCurrentTimes(time, lessonTimes), time))
        }
    }

    private fun ResultState.setSchedule2(schedule: List<DailySchedulePack>, rawSchedule: Schedule) {
        state = state.copy(schedule = schedule)
        setWeeks(rawSchedule)
        setCurrentTimes()

        setIsRefreshing(false)
        setIsLoading(false)
        setError(null)
    }

    private fun ResultState.setSchedule(scheduleSource: ScheduleSource?) {
        if (scheduleSource == null) {
            setError(ScheduleError.GroupNotSelected)
        } else {
            setIsLoading(true)
            scope.launch {
                combine(
                    useCase.getSchedule(scheduleSource),
                    useCase.getAllTags(),
                    useCase.getAllDeadlines()
                ) { scheduleResult, tagsResult, deadlinesResult ->
                    scheduleResult.onSuccess { schedule ->
                        val scheduleSettings = ScheduleSettings(
                            false,  // TODO FIX
                            LessonDateFilter.Default,   // TODO FIX
                            LessonFeaturesSettings.fromUserSchedule(scheduleSource)
                        )
                        val scheduleUiData = ScheduleUiData(
                            schedule,
                            tagsResult.getOrDefault(emptyList()),
                            deadlinesResult.getOrDefault(emptyMap()),
                            scheduleSettings,
                            state.dateFrom..state.dateTo
                        )
                        sendIntent(ScheduleIntent.SetSchedule(scheduleUiData, schedule))
                    }.onFailure {
                        sendIntent(ScheduleIntent.SetError(ScheduleError.ScheduleNotFound))
                    }
                }.collect()
            }
        }
    }

    private fun ResultState.setWeeks(schedule: Schedule) {
        val weeks = ScheduleDatesUiData(schedule, state.dateFrom..state.dateTo)
        if (state.weeks != weeks) {
            state = state.copy(weeks = weeks)
        }
    }

    private fun ResultState.setIsLoading(isLoading: Boolean) {
        if (!state.isRefreshing && state.isLoading != isLoading) {
            state = state.copy(isLoading = isLoading)
        }
    }

    private fun ResultState.setIsRefreshing(isRefreshing: Boolean) {
        if (state.isRefreshing != isRefreshing) {
            state = state.copy(isRefreshing = isRefreshing)
        }
    }

    private fun ResultState.setError(error: ScheduleError?) {
        if (state.error != error) {
            state = state.copy(error = error)
            if (error != null) {
                setIsRefreshing(false)
                setIsLoading(false)
            }
        }
    }

    private fun ResultState.setToday() {
        setDate(LocalDate.now())
    }

    private fun ResultState.setDate(date: LocalDate) {
        if (state.date != date) {
            state = state.copy(date = date)
            setDateIsToday(date)
            setSchedulePosition(state.dateFrom.until(date, ChronoUnit.DAYS).toInt())
            setWeekPosition((state.dateFrom.until(date.getOfThisWeek(DayOfWeek.MONDAY), ChronoUnit.DAYS) / 7).toInt())
            setDayOfWeekPosition(date.dayOfWeek.value - 1)
        }
    }

    private fun ResultState.setDateIsToday(date: LocalDate) {
        val dateIsToday = state.today == date
        if (state.dateIsToday != dateIsToday) {
            state = state.copy(dateIsToday = dateIsToday)
        }
    }

    private fun ResultState.setSchedulePosition(position: Int) {
        if (state.schedulePosition != position) {
            state = state.copy(schedulePosition = position)
            setDate(state.dateFrom.plusDays(position.toLong()))
        }
    }

    private fun ResultState.setWeekPosition(position: Int) {
        if (state.weekPosition != position) {
            state = state.copy(weekPosition = position)
        }
    }

    private fun ResultState.setDayOfWeekPosition(position: Int) {
        if (state.dayOfWeekPosition != position) {
            state = state.copy(dayOfWeekPosition = position)
        }
    }
}

data class ScheduleState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: ScheduleError? = null,

    val scheduleSource: ScheduleSource? = null,
    val schedule: List<DailySchedulePack> = emptyList(),
    val weeks: List<ScheduleWeekUiData> = emptyList(),

    val schedulePosition: Int = 0,
    val weekPosition: Int = 0,

    val dayOfWeekPosition: Int = 0,


    val date: LocalDate = LocalDate.MIN,
    val today: LocalDate = LocalDate.now(),
    val dateIsToday: Boolean = false,

    val currentLessonTimes: Pair<List<LessonTime>, LocalTime> = Pair(emptyList(), LocalTime.now())
) {
    companion object {
        fun init(): ScheduleState {
            val date = LocalDate.now()
            val dateFrom = ScheduleState().dateFrom
            val schedulePosition = dateFrom.until(date, ChronoUnit.DAYS).toInt()
            val weeksPosition = (dateFrom.until(date.getOfThisWeek(DayOfWeek.MONDAY), ChronoUnit.DAYS) / 7).toInt()
            val dayOfWeekPosition = date.dayOfWeek.value - 1
            return ScheduleState(
                date = date,
                schedulePosition = schedulePosition,
                weekPosition = weeksPosition,
                dayOfWeekPosition = dayOfWeekPosition,
                dateIsToday = true
            )
        }

    }

    val dateFrom
        get() = today.minusYears(1).getOfThisWeek(DayOfWeek.MONDAY)
    val dateTo
        get() = today.plusYears(1).getOfThisWeek(DayOfWeek.SUNDAY)

    val schedulePositionMax = dateFrom.until(dateTo, ChronoUnit.DAYS).toInt() - 1
    val weekPositionMax = schedulePositionMax / 7 - 1
    val dayOfWeekMax = 6
}

sealed interface ScheduleIntent {
    data class SelectScheduleSource(val scheduleSource: ScheduleSource?) : ScheduleIntent
    object SetToday : ScheduleIntent
    object RefreshSchedule : ScheduleIntent
    data class SetDate(val date: LocalDate) : ScheduleIntent
    data class SetSchedulePosition(val position: Int) : ScheduleIntent
    data class SetAdvancedSearch(val scheduleFilters: ScheduleFilters?) : ScheduleIntent
    object SetCurrentTimes : ScheduleIntent
    data class SetSchedule(val schedule: List<DailySchedulePack>, val rawSchedule: Schedule) : ScheduleIntent
    data class SetError(val error: ScheduleError?) : ScheduleIntent
}

enum class ScheduleError {
    GroupNotSelected,
    ScheduleNotFound
}

//val filteredSchedule: Schedule? = null,
//val scheduleSettings: ScheduleSettings? = null,
//val allLessonTypes: Set<String> = emptySet(),
//val lessonDateFilter: LessonDateFilter? = null,
//val schedule: Schedule? = null,
//val filterTypes: Set<String> = emptySet(),