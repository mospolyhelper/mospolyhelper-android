package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.AdvancedSearchScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleException
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleFilters
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleSource
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
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
    internal val store: Store<ScheduleState, ScheduleIntent, Nothing> =
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

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _advancedSearchUser = MutableStateFlow<ScheduleSource?>(null)

    val savedUsers = useCase.getSavedUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val user = useCase.getCurrentUser()
        .combine(_advancedSearchUser) { user, advancedSearchUser ->
            advancedSearchUser ?: user
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val scheduleLoadSignal = combine(loadDataSignal, user) { _, user -> user }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val schedule = scheduleLoadSignal.flatMapConcat { useCase.getSchedule(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    val filteredSchedule = combine(schedule, filterTypes) { schedule, filterTypes ->
        schedule.map { it.filter(types = filterTypes) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val tags = useCase.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val deadlines = useCase.getAllDeadlines()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    val lessonDateFilter = MutableStateFlow(useCase.getLessonDateFilter())

    private val scheduleSettings = combineTransform(useCase.getShowEmptyLessons(), lessonDateFilter, user) {
            showEmptyLessons, lessonDateFilter, user ->
        if (user != null) {
            emit(
                ScheduleSettings(
                    showEmptyLessons,
                    lessonDateFilter,
                    LessonFeaturesSettings.fromUserSchedule(user)
                )
            )
        } else {
            emit(null)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    val scheduleUiData = combineTransform(filteredSchedule, tags, deadlines, scheduleSettings) {
            schedule, tags, deadlines, scheduleSettings ->
        if (!schedule.isLoading && !tags.isLoading && !deadlines.isLoading) {
            schedule.onSuccess {
                if (scheduleSettings != null) {
                    emit(Result0.Success(
                        ScheduleUiData(
                            it,
                            tags.getOrDefault(emptyList()),
                            deadlines.getOrDefault(emptyMap()),
                            scheduleSettings
                        )
                    ))
                }
            }.onFailure {
                if (it !is ScheduleException.UserIsNull || scheduleSettings == null) {
                    emit(Result0.Failure(it))
                }
            }
        }
    }

    private val _schedulePosition = MutableStateFlow(0)
    val schedulePosition: StateFlow<Int> = _schedulePosition

    val scheduleDatesUiData = filteredSchedule.map {
        it.map { ScheduleDatesUiData(it) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val _scheduleWeekPosition = MutableStateFlow(0)
    val scheduleWeekPosition: StateFlow<Int> = _scheduleWeekPosition

    val isLoading: StateFlow<Boolean> = schedule.mapLatest { it.isLoading }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allLessonTypes = schedule.map { it.getOrNull()?.getAllTypes() ?: emptySet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())


    init {
        launchTimer()

        viewModelScope.launch {
            filteredSchedule.collect {
                it.onSuccess {
                    store.onIntent(ScheduleIntent.SetDates(it.dateFrom..it.dateTo))
                }.onFailure {
                    store.onIntent(ScheduleIntent.SetDates(LocalDate.now()..LocalDate.now()))
                }
            }
        }

        viewModelScope.launch {
            scheduleDatesUiData.collect {
                it.onSuccess {
                    store.onIntent(ScheduleIntent.SetDatesWeek(it.dates))
                }.onFailure {
                    store.onIntent(ScheduleIntent.SetDatesWeek(LocalDate.now()..LocalDate.now()))
                }
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
                    useCase.setCurrentUser(it.first())
                }
            }
        }

        viewModelScope.launch {
            lessonDateFilter.collect {
                useCase.setLessonDateFilter(it)
            }
        }

        viewModelScope.launch {
            filteredSchedule.collect {
                it.onSuccess {
                    setCurrentTimes()
                }
            }
        }

        viewModelScope.launch {
            store.statesFlow.collect {
                if (it.isAnyChanged { listOf(date, dates) }) {
                    _schedulePosition.value = store.state.dates.start.until(store.state.date, ChronoUnit.DAYS).toInt()
                }
                if (it.isAnyChanged { listOf(date, datesWeek) }) {
                    _scheduleWeekPosition.value = (store.state.datesWeek.start.until(store.state.date, ChronoUnit.DAYS) / 7L).toInt()
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
        if (this.user.value != source) {
            viewModelScope.launch {
                _advancedSearchUser.value = null
                useCase.setCurrentUser(source)
            }
        }
    }

    private suspend fun updateSchedule() {
        _advancedSearchUser.value = null
        useCase.updateSchedule(user.value)
    }

    fun setDay(day: Int) {
        store.onIntent(ScheduleIntent.SetDay(day))
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
        val lessonTimes = filteredSchedule.value.getOrNull()
            ?.getLessons(moscowLocalDate())?.map { it.time } ?: emptyList()
        val time = moscowLocalTime()
        _currentLessonTimes.value = Pair(LessonTimeUtils.getCurrentTimes(time, lessonTimes), time)
    }

    fun setRefreshing() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            updateSchedule()
            _isRefreshing.emit(false)
        }
    }

    fun setTodayDate() {
        store.onIntent(ScheduleIntent.SetDate(LocalDate.now()))
    }

    fun addTypeFilter(filter: String) {
        _filterTypes.value += filter
    }

    fun removeTypeFilter(filter: String) {
        _filterTypes.value -= filter
    }

    fun setWeek(position: Int) {
        _scheduleWeekPosition.value = position
    }
}

internal class ScheduleStore : Store<ScheduleState, ScheduleIntent, Nothing>(ScheduleState()) {
    override fun onIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.SetTodayDate -> setTodayDate()
            is ScheduleIntent.SetDate -> setDate(intent.date)
            is ScheduleIntent.SetDates -> setDates(intent.dates)
            is ScheduleIntent.SetDay -> setDay(intent.day)
            is ScheduleIntent.SetDatesWeek -> setDatesWeek(intent.datesWeek)
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
            state = state.copy(date = newDate)
        }
    }

    private fun getBoundedDate(date: LocalDate): LocalDate {
        return when {
            date < state.dates.start -> state.dates.start
            date > state.dates.endInclusive -> state.dates.endInclusive
            else -> date
        }
    }

    private fun setDates(dates: ClosedRange<LocalDate>) {
        if (state.dates != dates) {
            state = state.copy(dates = dates, date = getBoundedDate(state.date))
        }
    }

    private fun setDatesWeek(dates: ClosedRange<LocalDate>) {
        if (state.datesWeek != dates) {
            state = state.copy(datesWeek = dates)
        }
    }
}

internal data class ScheduleState(
    val scheduleSource: ScheduleSource? = null,
    val schedule: List<DailySchedulePack> = emptyList(),
    val dates: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
    val datesWeek: ClosedRange<LocalDate> = LocalDate.now()..LocalDate.now(),
    val date: LocalDate = LocalDate.now()
    )

internal sealed class ScheduleIntent {
    object SetTodayDate : ScheduleIntent()
    data class SetDay(val day: Int) : ScheduleIntent()
    data class SetDate(val date: LocalDate) : ScheduleIntent()
    data class SetDates(val dates: ClosedRange<LocalDate>) : ScheduleIntent()
    data class SetDatesWeek(val datesWeek: ClosedRange<LocalDate>) : ScheduleIntent()
}