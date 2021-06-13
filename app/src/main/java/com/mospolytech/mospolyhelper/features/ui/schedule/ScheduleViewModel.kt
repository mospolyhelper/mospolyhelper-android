package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.AdvancedSearchSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.ScheduleFilters
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.lesson.LessonTime
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.model.LessonFeaturesSettings
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleSettings
import com.mospolytech.mospolyhelper.features.ui.schedule.model.ScheduleUiData
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.time.LocalDate
import java.time.LocalTime


class ScheduleViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModel(), KoinComponent {

    val date = MutableStateFlow<LocalDate>(LocalDate.now())
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

    private val _advancedSearchUser = MutableStateFlow<UserSchedule?>(null)

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

    private val scheduleSettings = combine(useCase.getShowEmptyLessons(), lessonDateFilter, user.filterNotNull()) {
            showEmptyLessons, lessonDateFilter, user ->
        ScheduleSettings(
            showEmptyLessons,
            lessonDateFilter,
            LessonFeaturesSettings.fromUserSchedule(user)
        )
    }.distinctUntilChanged()


    val scheduleUiData = combineTransform(filteredSchedule, tags, deadlines, scheduleSettings) {
            schedule, tags, deadlines, scheduleSettings ->
        if (!schedule.isLoading && !tags.isLoading && !deadlines.isLoading) {
            schedule.onSuccess {
                emit(Result0.Success(
                    ScheduleUiData(
                        it,
                        tags.getOrDefault(emptyList()),
                        deadlines.getOrDefault(emptyMap()),
                        scheduleSettings
                    )
                ))
            }.onFailure {
                emit(Result0.Failure(it))
            }
        }
    }

    val isLoading: StateFlow<Boolean> = schedule.mapLatest { it.isLoading }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allLessonTypes = schedule.map { it.getOrNull()?.getAllTypes() ?: emptySet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val dates = filteredSchedule.transform {
        it.onSuccess {
            emit(it.dateFrom..it.dateTo)
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
    }


    fun setAdvancedSearch(filters: ScheduleFilters) {
        viewModelScope.launch {
            _advancedSearchUser.value = AdvancedSearchSchedule(filters)
        }
    }

    fun setUser(user: UserSchedule?) {
        if (this.user.value != user) {
            viewModelScope.launch {
                _advancedSearchUser.value = null
                useCase.setCurrentUser(user)
            }
        }
    }

    private suspend fun updateSchedule() {
        _advancedSearchUser.value = null
        useCase.updateSchedule(user.value)
    }

    fun setDay(day: Int) {
        date.value = filteredSchedule.value.getOrNull()?.dateFrom?.plusDays(day.toLong())
                ?: LocalDate.now()
    }

    fun getDateByDay(day: Long): LocalDate {
        return filteredSchedule.value.getOrNull()?.dateFrom?.plusDays(day)
                ?: LocalDate.now()
    }

    fun removeUser(user: UserSchedule) {
        viewModelScope.launch {
            useCase.removeSavedScheduleUser(user)
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

    suspend fun setRefreshing() {
        _isRefreshing.emit(true)
        updateSchedule()
        _isRefreshing.emit(false)
    }

    fun setTodayDate() {
        date.value = LocalDate.now()
    }

    fun addTypeFilter(filter: String) {
        _filterTypes.value += filter
    }

    fun removeTypeFilter(filter: String) {
        _filterTypes.value -= filter
    }
}


