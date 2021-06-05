package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import java.time.LocalDate
import java.time.LocalTime


class ScheduleViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModelBase(mediator, ScheduleViewModel::class.java.simpleName), KoinComponent {

    companion object {
        const val MessageChangeDate = "ChangeDate"
        const val MessageSetAdvancedSearchSchedule = "SetAdvancedSearchSchedule"
    }


    val date = MutableStateFlow<LocalDate>(LocalDate.now())
    val currentLessonTimes: MutableStateFlow<Pair<List<LessonTime>, LocalTime>> =
        MutableStateFlow(Pair(emptyList(), LocalTime.now()))

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

    private val userLoadSignal = useCase.getCurrentUser()
        .combine(_advancedSearchUser) { user, advancedSearchUser ->
            advancedSearchUser ?: user
        }.distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    val user = userLoadSignal.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val scheduleLoadSignal = combine(loadDataSignal, userLoadSignal) { _, user -> user}
        .shareIn(viewModelScope, SharingStarted.Eagerly)


    // Refresh schedule when needed and when the user changes
    private val schedule = scheduleLoadSignal.flatMapConcat { useCase.getSchedule(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val _filterTypes = MutableStateFlow<Set<String>>(emptySet())
    val filterTypes: StateFlow<Set<String>> = _filterTypes

    val filteredSchedule = schedule.combine(filterTypes) { schedule, filterTypes ->
        schedule.map { it.filter(types = filterTypes) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val tags = useCase.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)

    private val deadlines = useCase.getAllDeadlines()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result0.Loading)


    private val showEmptyLessons = MutableStateFlow(useCase.getShowEmptyLessons())

    val lessonDateFilter = MutableStateFlow(useCase.getLessonDateFilter())

    val scheduleSettings = combine(showEmptyLessons, lessonDateFilter, user) {
            showEmptyLessons, lessonDateFilter, user ->
        ScheduleSettings(
            showEmptyLessons,
            lessonDateFilter,
            LessonFeaturesSettings.fromUserSchedule(user)
        )
    }


    val scheduleUiData = combineTransform(filteredSchedule, tags, deadlines, scheduleSettings) {
            schedule, tags, deadlines, scheduleSettings ->
        if (!schedule.isLoading && !tags.isLoading && !deadlines.isLoading) {
            emit(Result0.Success(
                ScheduleUiData(
                    schedule.getOrNull(),
                    tags.getOrDefault(emptyList()),
                    deadlines.getOrDefault(emptyMap()),
                    scheduleSettings
                )
            ))
        }
    }

    val isLoading: StateFlow<Boolean> = schedule.mapLatest { it.isLoading }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allLessonTypes = schedule.map { it.getOrNull()?.getAllTypes() ?: emptySet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())


    init {
        subscribe(::handleMessage)
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
            lessonDateFilter.collect {
                useCase.setLessonDateFilter(it)
            }
        }

        viewModelScope.launch {
            showEmptyLessons.collect {
                useCase.setShowEmptyLessons(it)
            }
        }

        viewModelScope.launch {
            filteredSchedule.collect {
                setCurrentTimes()
            }
        }
    }


    // TODO: Remove
    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            MessageChangeDate -> {
                date.value = message.content[0] as LocalDate
            }
            MessageSetAdvancedSearchSchedule -> {
                viewModelScope.launch {
                    _advancedSearchUser.value = AdvancedSearchSchedule(
                        message.content[0] as ScheduleFilters
                    )
                }
            }
        }
    }

    suspend fun setUser(user: UserSchedule?) {
        _advancedSearchUser.value = null
        useCase.setCurrentUser(user)
    }

    private suspend fun updateSchedule() {
        _advancedSearchUser.value = null
        useCase.updateSchedule(user.value)
    }

    suspend fun removeUser(user: UserSchedule) {
        useCase.removeSavedScheduleUser(user)
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
        currentLessonTimes.value = Pair(LessonTimeUtils.getCurrentTimes(time, lessonTimes), time)
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


