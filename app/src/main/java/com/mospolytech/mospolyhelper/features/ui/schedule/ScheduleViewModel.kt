package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
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
import kotlin.coroutines.CoroutineContext


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

    private val dates = filteredSchedule.transform {
        it.onSuccess {
            emit(it.dateFrom..it.dateTo)
        }.onFailure {
            emit(LocalDate.now()..LocalDate.now())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, LocalDate.now()..LocalDate.now())

    private val datesWeeks = scheduleDatesUiData.transform {
        it.onSuccess {
            emit(it.dates)
        }.onFailure {
            emit(LocalDate.now()..LocalDate.now())
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, LocalDate.now()..LocalDate.now())


    init {
        launchTimer()

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
            dates.collect {
                setDate(store.state.date)
            }
        }

        viewModelScope.launch {
            combine(datesWeeks, store.statesFlow) { dates, date ->
                _scheduleWeekPosition.value = (dates.start.until(date.date, ChronoUnit.DAYS) / 7L).toInt()
            }.collect()
        }

        viewModelScope.launch {
            combine(dates, store.statesFlow) { dates, date ->
                _schedulePosition.value = dates.start.until(date.date, ChronoUnit.DAYS).toInt()
            }.collect()
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
        setDate(dates.value.start.plusDays(day.toLong()))
    }

    fun getDateByDay(day: Long): LocalDate {
        return dates.value.start.plusDays(day)
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

    private fun setDate(date: LocalDate) {
        val newDate = when {
            date < dates.value.start -> dates.value.start
            date > dates.value.endInclusive -> dates.value.endInclusive
            else -> date
        }
        store.onIntent(ScheduleIntent.SetDate(newDate))
    }

    fun setTodayDate() {
        setDate(LocalDate.now())
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

    fun setSchedulePosition(position: Int) {
        _schedulePosition.value = position
    }
}

internal class ScheduleStore : Store<ScheduleState, ScheduleIntent, Nothing>(ScheduleState()) {
    override fun onIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.SetScheduleSource -> TODO()
            is ScheduleIntent.SetTodayDate -> setTodayDate()
            is ScheduleIntent.SetDate -> setDate(intent.date)
        }
    }

    private fun setScheduleSource() {

    }

    private fun setTodayDate() {
        val today = LocalDate.now()
        if (state.date != today) {
            state = state.copy(date = today)
        }
    }

    private fun setDate(date: LocalDate) {
        if (state.date != date) {
            state = state.copy(date = date)
        }
    }
}

internal data class ScheduleState(
    val scheduleSource: ScheduleSource? = null,
    val schedule: List<DailySchedulePack> = emptyList(),
    val date: LocalDate = LocalDate.now()
    )

internal sealed class ScheduleIntent {
    object SetScheduleSource : ScheduleIntent()
    object SetTodayDate : ScheduleIntent()
    data class SetDate(val date: LocalDate) : ScheduleIntent()
}