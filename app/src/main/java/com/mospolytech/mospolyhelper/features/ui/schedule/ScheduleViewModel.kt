package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.features.ui.schedule.model.*
import com.mospolytech.mospolyhelper.utils.*
import com.tipapro.mvilight.coroutines.boundWith
import com.tipapro.mvilight.main.Store
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalTime


class ScheduleViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    internal val store: Store<ScheduleState, ScheduleIntent, Nothing> =
        ScheduleStore().boundWith(viewModelScope)

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
            useCase.removeSavedScheduleUser(source)
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



internal class ScheduleStore : Store<ScheduleState, ScheduleIntent, Nothing>(ScheduleState()) {

    override fun ResultState.processIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.SetSelectedScheduleSource -> setSelectedScheduleSource(intent.scheduleSource)
        }
    }

    private fun ResultState.setSelectedScheduleSource(selectedScheduleSource: ScheduleSource?) {
        if (state.selectedScheduleSource != selectedScheduleSource) {
            state = state.copy(selectedScheduleSource = selectedScheduleSource)
        }
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
//    private fun setIsRefreshing(isRefreshing: Boolean) {
//        if (state.isRefreshing != isRefreshing) {
//            state = state.copy(isRefreshing = isRefreshing && !state.isLoading)
//        }
//    }
//
//    private fun setIsLoading(isLoading: Boolean) {
//        if (state.isLoading != isLoading) {
//            state = state.copy(isLoading = isLoading && !state.isRefreshing)
//        }
//    }
//
//    private fun setException(exception: Throwable?) {
//        if (state.exception != exception) {
//            state = state.copy(exception = exception)
//        }
//    }
//
//    private fun setScheduleUiData(scheduleUiData: ScheduleUiData?) {
//        if (state.scheduleUiData != scheduleUiData) {
//            state = state.copy(scheduleUiData = scheduleUiData)
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
//    private fun setScheduleSettings(scheduleSettings: ScheduleSettings?) {
//        if (state.scheduleSettings != scheduleSettings) {
//            state = state.copy(
//                scheduleSettings = scheduleSettings
//            )
//        }
//    }
//
//    private fun setTags(tags: List<LessonTag>) {
//        if (state.tags != tags) {
//            state = state.copy(
//                tags = tags
//            )
//        }
//    }
//    private fun setDeadlines(deadlines: Map<String, List<Deadline>>) {
//        if (state.deadlines != deadlines) {
//            state = state.copy(
//                deadlines = deadlines
//            )
//        }
//    }
//
//    private fun setAllLessonTypes(allLessonTypes: Set<String>) {
//        if (state.allLessonTypes != allLessonTypes) {
//            state = state.copy(allLessonTypes = allLessonTypes)
//        }
//    }
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
}

internal data class ScheduleState(
    val selectedScheduleSource: ScheduleSource? = null,
    )

internal sealed interface ScheduleIntent {
    data class SetSelectedScheduleSource(val scheduleSource: ScheduleSource) : ScheduleIntent
}

//val dates: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
//val datesWeek: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
//val date: LocalDate = LocalDate.now(),
//val schedulePosition: Int = 0,
//val scheduleWeekPosition: Int = 0,
//val isRefreshing: Boolean = false,
//val isLoading: Boolean = true,
//val exception: Throwable? = null,
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