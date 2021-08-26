package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.deadline.model.Deadline
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.model.tag.LessonTag
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.features.ui.schedule.model.*
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class ScheduleViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    internal val store: Store<ScheduleState, ScheduleIntent, Nothing, Nothing> =
        ScheduleStore().boundWith(viewModelScope)

    private val _currentLessonTimes = MutableStateFlow(Pair(emptyList<LessonTime>(), LocalTime.now()))
    val currentLessonTimes: StateFlow<Pair<List<LessonTime>, LocalTime>> = _currentLessonTimes

    private val _filterTypes = MutableStateFlow<Set<String>>(emptySet())
    val filterTypes: StateFlow<Set<String>> = _filterTypes

    // Used to re-run flows on command
    private val refreshSignal = MutableSharedFlow<Unit>()
    // Used to run flows on init and also on command
    private val loadDataSignal: Flow<Unit> = flow {
        emit(Unit)
        emitAll(refreshSignal)
    }

    private val _advancedSearchUser = MutableStateFlow<ScheduleSource?>(null)

    val savedUsers = useCase.getFavoriteScheduleSources()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val selectedScheduleSource = useCase.getSelectedScheduleSource()
        .combine(_advancedSearchUser) { user, advancedSearchUser ->
            advancedSearchUser ?: user
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val scheduleLoadSignal = combine(loadDataSignal, selectedScheduleSource) { _, user -> user }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val schedule = scheduleLoadSignal.flatMapConcat { useCase.getSchedule(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    val lessonDateFilter = MutableStateFlow(useCase.getLessonDateFilter())

    val allLessonTypes = schedule.map { it.getOrNull()?.getAllTypes() ?: emptySet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())


    init {
        launchTimer()

        viewModelScope.launch {
            useCase.getAllTags().collect {
                store.sendIntent(ScheduleIntent.SetTags(it.getOrNull() ?: emptyList()))
            }
        }
        viewModelScope.launch {
            useCase.getAllDeadlines().collect {
                store.sendIntent(ScheduleIntent.SetDeadlines(it.getOrNull() ?: emptyMap()))
            }
        }

        viewModelScope.launch {
            schedule.collectLatest {
                store.sendIntent(ScheduleIntent.SetIsLoading(it.isLoading))
            }
        }

        viewModelScope.launch {
            useCase.scheduleUpdates.collect {
                refreshSignal.emit(Unit)
            }
        }

        viewModelScope.launch {
            schedule.collect {
                it.onSuccess {
                    _filterTypes.value = filterTypes.value.intersect(it.getAllTypes())
                }.onFailure {
                    _filterTypes.value = emptySet()
                }
            }
        }

        viewModelScope.launch {
            savedUsers.collect {
                if (it.size == 1) {
                    useCase.setSelectedScheduleSource(it.first())
                }
            }
        }

        viewModelScope.launch {
            lessonDateFilter.collect {
                useCase.setLessonDateFilter(it)
            }
        }

        viewModelScope.launch {
            combine(schedule, filterTypes) { schedule, filterTypes ->
                schedule.map { it.filter(types = filterTypes) }
                    .onSuccess {
                        store.sendIntent(ScheduleIntent.SetFilteredSchedule(it))
                    }.onFailure {
                        if (it !is ScheduleException.UserIsNull || store.state.scheduleSettings == null) {
                            store.sendIntent(ScheduleIntent.SetException(it))
                        }
                    }

            }.collect()
        }

        viewModelScope.launch {
            combine(useCase.getShowEmptyLessons(), lessonDateFilter, selectedScheduleSource) {
                    showEmptyLessons, lessonDateFilter, user ->
                if (user != null) {
                    store.sendIntent(ScheduleIntent.SetScheduleSettings(ScheduleSettings(
                        showEmptyLessons,
                        lessonDateFilter,
                        LessonFeaturesSettings.fromUserSchedule(user)
                    )))
                } else {
                    store.sendIntent(ScheduleIntent.SetScheduleSettings(null))
                }
            }.collect()
        }
        var currentState: ScheduleState? = null
        viewModelScope.launch {
            store.statesFlow.collect {
                val state = StatePair(currentState, it)
                currentState = it

                if (state.isChanged { filteredSchedule }) {
                    state.new.filteredSchedule?.let {
                        setCurrentTimes()
                    }
                }

                if (state.isAnyChanged { listOf(filteredSchedule, scheduleSettings, tags, deadlines) }) {
                    if (state.new.filteredSchedule != null && state.new.scheduleSettings != null) {
                        store.sendIntent(
                            ScheduleIntent.SetScheduleUiData(
                                ScheduleUiData(
                                state.new.filteredSchedule,
                                state.new.tags,
                                state.new.deadlines,
                                state.new.scheduleSettings
                            )
                            )
                        )
                    }
                }
            }
        }
    }


    fun setAdvancedSearch(filters: ScheduleFilters) {
        viewModelScope.launch {
            _advancedSearchUser.value = AdvancedSearchScheduleSource(filters)
        }
    }

    fun setUser(source: ScheduleSource?) {
        if (this.selectedScheduleSource.value != source) {
            viewModelScope.launch {
                _advancedSearchUser.value = null
                useCase.setSelectedScheduleSource(source)
            }
        }
    }

    private suspend fun updateSchedule() {
        _advancedSearchUser.value = null
        useCase.updateSchedule(selectedScheduleSource.value)
    }

    fun setDay(day: Int) {
        store.sendIntent(ScheduleIntent.SetDay(day))
    }

    fun getDateByDay(day: Long): LocalDate {
        return store.state.dates.start.plusDays(day)
    }

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
        val lessonTimes = store.state.filteredSchedule
            ?.getLessons(moscowLocalDate())?.map { it.time } ?: emptyList()
        val time = moscowLocalTime()
        _currentLessonTimes.value = Pair(LessonTimeUtils.getCurrentTimes(time, lessonTimes), time)
    }

    fun setRefreshing() {
        viewModelScope.launch {
            store.sendIntent(ScheduleIntent.SetIsRefreshing(true))
            updateSchedule()
            store.sendIntent(ScheduleIntent.SetIsRefreshing(false))
        }
    }

    fun setTodayDate() {
        store.sendIntent(ScheduleIntent.SetDate(LocalDate.now()))
    }

    fun addTypeFilter(filter: String) {
        _filterTypes.value += filter
    }

    fun removeTypeFilter(filter: String) {
        _filterTypes.value -= filter
    }

    fun onScheduleWeekPosition(position: Int) {
        store.sendIntent(ScheduleIntent.SetScheduleWeekPosition(position))
    }
}



// ScheduleStore
internal class ScheduleStore : Store<ScheduleState, ScheduleIntent, Nothing, Nothing>(ScheduleState()) {
    override fun processIntent(intent: ScheduleIntent) {
        Log.d("Store::Intent", intent.toString())
        when (intent) {
            is ScheduleIntent.SetTodayDate -> setTodayDate()
            is ScheduleIntent.SetDate -> setDate(intent.date)
            is ScheduleIntent.SetDates -> setDates(intent.dates)
            is ScheduleIntent.SetDay -> setDay(intent.day)
            is ScheduleIntent.SetDatesWeek -> setDatesWeek(intent.datesWeek)
            is ScheduleIntent.SetScheduleWeekPosition -> setScheduleWeekPosition(intent.position)
            is ScheduleIntent.SetIsLoading -> setIsLoading(intent.isLoading)
            is ScheduleIntent.SetIsRefreshing -> setIsRefreshing(intent.isRefreshing)
            is ScheduleIntent.SetException -> setException(intent.exception)
            is ScheduleIntent.SetScheduleUiData -> setScheduleUiData(intent.scheduleUiData)
            is ScheduleIntent.SetFilteredSchedule -> setFilteredSchedule(intent.filteredSchedule)
            is ScheduleIntent.SetScheduleSettings -> setScheduleSettings(intent.scheduleSettings)
            is ScheduleIntent.SetDeadlines -> setDeadlines(intent.deadlines)
            is ScheduleIntent.SetTags -> setTags(intent.tags)
        }
    }

    private fun setTodayDate() {
        setDate(LocalDate.now())
    }

    private fun setDay(day: Int) {
        setDate(state.dates.start.plusDays(day.toLong()))
    }

    private fun setDate(date: LocalDate) {
        val newDate = getBoundedDate(date)
        if (state.date != newDate) {
            val newSchedulePosition = state.dates.start.until(newDate, ChronoUnit.DAYS).toInt()
            val newScheduleWeekPosition = (state.datesWeek.start.until(newDate, ChronoUnit.DAYS) / 7L).toInt()
            state = state.copy(
                date = newDate,
                schedulePosition = newSchedulePosition,
                scheduleWeekPosition = newScheduleWeekPosition
            )
        }
    }

    private fun getBoundedDate(date: LocalDate): LocalDate {
        return when {
            date < state.dates.start -> state.dates.start
            date > state.dates.endInclusive -> state.dates.endInclusive
            else -> date
        }
    }

    private fun getBoundedDate(date: LocalDate, dates: ClosedRange<LocalDate>): LocalDate {
        return when {
            date < dates.start -> dates.start
            date > dates.endInclusive -> dates.endInclusive
            else -> date
        }
    }

    private fun setDates(dates: ClosedRange<LocalDate>) {
        if (state.dates != dates) {
            val newDate = getBoundedDate(state.date)
            val newSchedulePosition = dates.start.until(newDate, ChronoUnit.DAYS).toInt()
            state = state.copy(dates = dates, date = newDate, schedulePosition = newSchedulePosition)
        }
    }

    private fun setDatesWeek(datesWeek: ClosedRange<LocalDate>) {
        if (state.datesWeek != datesWeek) {
            val newScheduleWeekPosition = (datesWeek.start.until(state.date, ChronoUnit.DAYS) / 7L).toInt()
            state = state.copy(datesWeek = datesWeek, scheduleWeekPosition = newScheduleWeekPosition)
        }
    }

    private fun setScheduleWeekPosition(position: Int) {
        if (state.scheduleWeekPosition != position) {
            state = state.copy(scheduleWeekPosition = position)
        }
    }

    private fun setIsRefreshing(isRefreshing: Boolean) {
        if (state.isRefreshing != isRefreshing) {
            state = state.copy(isRefreshing = isRefreshing && !state.isLoading)
        }
    }

    private fun setIsLoading(isLoading: Boolean) {
        if (state.isLoading != isLoading) {
            state = state.copy(isLoading = isLoading && !state.isRefreshing)
        }
    }

    private fun setException(exception: Throwable?) {
        if (state.exception != exception) {
            state = state.copy(exception = exception)
        }
    }

    private fun setScheduleUiData(scheduleUiData: ScheduleUiData?) {
        if (state.scheduleUiData != scheduleUiData) {
            state = state.copy(scheduleUiData = scheduleUiData)
        }
    }

    private fun setFilteredSchedule(filteredSchedule: Schedule?) {
        if (state.filteredSchedule != filteredSchedule) {
            val scheduleDatesUiData = ScheduleDatesUiData(filteredSchedule)
            val newDates = filteredSchedule?.let { it.dateFrom..it.dateTo } ?: LocalDate.now()..LocalDate.now()
            val newDatesWeek = scheduleDatesUiData.dates
            val newDate = getBoundedDate(state.date, newDates)
            val newSchedulePosition = newDates.start.until(newDate, ChronoUnit.DAYS).toInt()
            state = state.copy(
                filteredSchedule = filteredSchedule,
                scheduleDatesUiData = scheduleDatesUiData,
                dates = newDates,
                date = newDate,
                schedulePosition = newSchedulePosition,
                datesWeek = newDatesWeek
            )
        }
    }

    private fun setScheduleSettings(scheduleSettings: ScheduleSettings?) {
        if (state.scheduleSettings != scheduleSettings) {
            state = state.copy(
                scheduleSettings = scheduleSettings
            )
        }
    }

    private fun setTags(tags: List<LessonTag>) {
        if (state.tags != tags) {
            state = state.copy(
                tags = tags
            )
        }
    }
    private fun setDeadlines(deadlines: Map<String, List<Deadline>>) {
        if (state.deadlines != deadlines) {
            state = state.copy(
                deadlines = deadlines
            )
        }
    }

    override fun processEvent(event: Nothing) { }
}

internal data class ScheduleState(
    val dates: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
    val datesWeek: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
    val date: LocalDate = LocalDate.now(),
    val schedulePosition: Int = 0,
    val scheduleWeekPosition: Int = 0,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true,
    val exception: Throwable? = null,
    val scheduleDatesUiData: ScheduleDatesUiData? = null,
    val scheduleUiData: ScheduleUiData? = null,
    val filteredSchedule: Schedule? = null,
    val scheduleSettings: ScheduleSettings? = null,
    val tags: List<LessonTag> = emptyList(),
    val deadlines: Map<String, List<Deadline>> = emptyMap()
    )

internal sealed interface ScheduleIntent {
    object SetTodayDate : ScheduleIntent
    data class SetDay(val day: Int) : ScheduleIntent
    data class SetDate(val date: LocalDate) : ScheduleIntent
    data class SetDates(val dates: ClosedRange<LocalDate>) : ScheduleIntent
    data class SetDatesWeek(val datesWeek: ClosedRange<LocalDate>) : ScheduleIntent
    data class SetScheduleWeekPosition(val position: Int) : ScheduleIntent
    data class SetIsRefreshing(val isRefreshing: Boolean) : ScheduleIntent
    data class SetIsLoading(val isLoading: Boolean) : ScheduleIntent
    data class SetException(val exception: Throwable) : ScheduleIntent
    data class SetScheduleUiData(val scheduleUiData: ScheduleUiData?) : ScheduleIntent
    data class SetFilteredSchedule(val filteredSchedule: Schedule?) : ScheduleIntent
    data class SetScheduleSettings(val scheduleSettings: ScheduleSettings?) : ScheduleIntent
    data class SetTags(val tags: List<LessonTag>) : ScheduleIntent
    data class SetDeadlines(val deadlines: Map<String, List<Deadline>>) : ScheduleIntent
}