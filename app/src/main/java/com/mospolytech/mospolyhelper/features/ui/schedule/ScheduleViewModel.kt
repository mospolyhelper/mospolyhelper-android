package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonDateFilter
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
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
    private val useCase: ScheduleUseCase
) : ViewModel() {

    val store = ScheduleStore(useCase).boundWith(viewModelScope)

    private val _currentLessonTimes = MutableStateFlow(Pair(emptyList<LessonTime>(), LocalTime.now()))
    val currentLessonTimes: StateFlow<Pair<List<LessonTime>, LocalTime>> = _currentLessonTimes


    init {
        launchTimer()
    }


//    fun getDateByDay(day: Long): LocalDate {
//        return store.state.dates.start.plusDays(day)
//    }

    fun removeUser(source: ScheduleSource) {
        viewModelScope.launch {
            useCase.removeFavoriteScheduleSource(source)
        }
    }

    private fun launchTimer() {
        viewModelScope.launch {
            while (isActive) {
                setCurrentTimes()
                delay((60L - LocalTime.now().second) * 1000L)
            }
        }
    }

    private fun setCurrentTimes() {
//        val lessonTimes = store.state.filteredSchedule
//            ?.getLessons(moscowLocalDate())?.map { it.time } ?: emptyList()
//        val time = moscowLocalTime()
//        _currentLessonTimes.value = Pair(LessonTimeUtils.getCurrentTimes(time, lessonTimes), time)
    }

//    fun setRefreshing() {
//        viewModelScope.launch {
//            store.sendIntent(ScheduleIntent.SetIsRefreshing(true))
//            updateSchedule()
//            store.sendIntent(ScheduleIntent.SetIsRefreshing(false))
//        }
//    }
//
//    fun setTodayDate() {
//        store.sendIntent(ScheduleIntent.SetDate(LocalDate.now()))
//    }
//
//    fun onScheduleWeekPosition(position: Int) {
//        store.sendIntent(ScheduleIntent.SetScheduleWeekPosition(position))
//    }
}





//    private fun removeFilterType(filterType: String) {
//        state = state.copy(filterTypes = state.filterTypes - filterType)
//    }
//
//    private fun addFilterType(filterType: String) {
//        state = state.copy(filterTypes = state.filterTypes + filterType)
//    }
//
//    private fun setTodayDate() {
//        setDate(LocalDate.now())
//    }
//
//    private fun setDay(day: Int) {
//        setDate(state.dates.start.plusDays(day.toLong()))
//    }
//
//    private fun setDate(date: LocalDate) {
//        val newDate = getBoundedDate(date)
//        if (state.date != newDate) {
//            val newSchedulePosition = state.dates.start.until(newDate, ChronoUnit.DAYS).toInt()
//            val newScheduleWeekPosition = (state.datesWeek.start.until(newDate, ChronoUnit.DAYS) / 7L).toInt()
//            state = state.copy(
//                date = newDate,
//                schedulePosition = newSchedulePosition,
//                scheduleWeekPosition = newScheduleWeekPosition
//            )
//        }
//    }
//
//    private fun getBoundedDate(date: LocalDate): LocalDate {
//        return when {
//            date < state.dates.start -> state.dates.start
//            date > state.dates.endInclusive -> state.dates.endInclusive
//            else -> date
//        }
//    }
//
//    private fun getBoundedDate(date: LocalDate, dates: ClosedRange<LocalDate>): LocalDate {
//        return when {
//            date < dates.start -> dates.start
//            date > dates.endInclusive -> dates.endInclusive
//            else -> date
//        }
//    }
//
//    private fun setDates(dates: ClosedRange<LocalDate>) {
//        if (state.dates != dates) {
//            val newDate = getBoundedDate(state.date)
//            val newSchedulePosition = dates.start.until(newDate, ChronoUnit.DAYS).toInt()
//            state = state.copy(dates = dates, date = newDate, schedulePosition = newSchedulePosition)
//        }
//    }
//
//    private fun setDatesWeek(datesWeek: ClosedRange<LocalDate>) {
//        if (state.datesWeek != datesWeek) {
//            val newScheduleWeekPosition = (datesWeek.start.until(state.date, ChronoUnit.DAYS) / 7L).toInt()
//            state = state.copy(datesWeek = datesWeek, scheduleWeekPosition = newScheduleWeekPosition)
//        }
//    }
//
//    private fun setScheduleWeekPosition(position: Int) {
//        if (state.scheduleWeekPosition != position) {
//            state = state.copy(scheduleWeekPosition = position)
//        }
//    }
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
) : Store<ScheduleState, ScheduleIntent, Nothing>(ScheduleState.Init()) {

    init {
        scope.launch {
            useCase.getSelectedScheduleSource().collect {
                sendIntent(ScheduleIntent.SelectScheduleSource(it))
            }
        }

        sendIntent(ScheduleIntent.SetToday)
    }

    override fun ResultState.processIntent(intent: ScheduleIntent) {
        scope.launch {
            when (intent) {
                is ScheduleIntent.SelectScheduleSource -> selectScheduleSource(intent.scheduleSource)
                ScheduleIntent.SetToday -> setToday()
                ScheduleIntent.RefreshSchedule -> refreshSchedule()
                is ScheduleIntent.SetDate -> setDate(intent.date)
                is ScheduleIntent.SetSchedulePosition -> setSchedulePosition(intent.position)
            }
            commitState()
        }
    }

    private suspend fun ResultState.selectScheduleSource(scheduleSource: ScheduleSource?) {
        if (state.scheduleSource != scheduleSource) {
            state = state.copy(scheduleSource = scheduleSource)
            setSchedule(scheduleSource)
        }
    }

    private suspend fun ResultState.refreshSchedule() {
        setIsRefreshing(true)
        state.scheduleSource?.let {
            setSchedule(it)
        }
    }

    private suspend fun ResultState.setSchedule(scheduleSource: ScheduleSource?) {
        if (scheduleSource == null) {
            setIsRefreshing(false)
            setIsLoading(false)
            setError(ScheduleError.GroupNotSelected)
        } else {
            setIsLoading(true)
            commitState()
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
                    state = state.copy(
                        schedule = ScheduleUiData(
                            schedule,
                            tagsResult.getOrDefault(emptyList()),
                            deadlinesResult.getOrDefault(emptyMap()),
                            scheduleSettings,
                            state.dateFrom..state.dateTo
                        )
                    )

                    setWeeks(schedule)

                    setIsRefreshing(false)
                    setIsLoading(false)
                    setError(null)
                }.onFailure {
                    setIsRefreshing(false)
                    setIsLoading(false)
                    // TODO: if not found
                }
                commitState()
            }.collect()
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
    val isLoading: Boolean = false,
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
    val dateIsToday: Boolean = false
) {
    companion object {
        fun Init(): ScheduleState {
            val date = LocalDate.now()
            val dateFrom = ScheduleState().dateFrom
            val schedulePosition = dateFrom.until(date, ChronoUnit.DAYS).toInt()
            val weeksPosition = (dateFrom.until(date.getOfThisWeek(DayOfWeek.MONDAY), ChronoUnit.DAYS) / 7).toInt()
            val dayOfWeekPosition = date.dayOfWeek.value - 1
            return ScheduleState(
                date = date,
                schedulePosition = schedulePosition,
                weekPosition = weeksPosition,
                dayOfWeekPosition = dayOfWeekPosition
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
}

enum class ScheduleError {
    GroupNotSelected
}





//val dates: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
//val datesWeek: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
//val date: LocalDate = LocalDate.now(),
//val schedulePosition: Int = 0,
//val scheduleWeekPosition: Int = 0,
//val scheduleDatesUiData: ScheduleDatesUiData? = null,
//val scheduleUiData: ScheduleUiData? = null,
//val filteredSchedule: Schedule? = null,
//val scheduleSettings: ScheduleSettings? = null,
//val tags: List<LessonTag> = emptyList(),
//val deadlines: Map<String, List<Deadline>> = emptyMap(),
//val allLessonTypes: Set<String> = emptySet(),
//val lessonDateFilter: LessonDateFilter? = null,
//val schedule: Schedule? = null,
//val filterTypes: Set<String> = emptySet(),
//val selectedScheduleSource: ScheduleSource? = null,
//val favoriteScheduleSources: Set<ScheduleSource> = emptySet()