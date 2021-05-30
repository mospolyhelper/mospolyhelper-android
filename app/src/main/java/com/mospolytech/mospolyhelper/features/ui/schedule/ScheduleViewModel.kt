package com.mospolytech.mospolyhelper.features.ui.schedule

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleTagsDeadline
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.domain.schedule.utils.LessonTimeUtils
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
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


    private val _advancedSearchUser = MutableStateFlow<UserSchedule?>(null)

    val savedUsers = useCase.getSavedUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val user = useCase.getCurrentUser()
        .combine(_advancedSearchUser) { user, advancedSearchUser ->
            advancedSearchUser ?: user
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val lessonDateFilter = MutableStateFlow(useCase.getLessonDateFilter())

    private val originalSchedule = user.transform { value ->
        emit(ResultState.Loading)
        val q = useCase.getScheduleWithFeatures(value).map { Result2.success(it).toState() }
        emit(q.first())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResultState.Loading)

    val allLessonTypes = originalSchedule.map {
        (it as? ResultState.Ready)?.result?.getOrNull()?.schedule?.getAllTypes() ?: emptySet()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val filterTypes = MutableStateFlow<Set<String>>(emptySet())

    val filteredSchedule = originalSchedule.combine(filterTypes) { state, filterTypes ->
        state.onReady {
            it.onSuccess {
                Result2.success(
                    it.copy(schedule = it.schedule?.filter(types = filterTypes))
                ).toState()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ResultState.Loading)


    val showEmptyLessons = MutableStateFlow(useCase.getShowEmptyLessons())

    val onMessage: Event1<String> = Action1()

    init {
        subscribe(::handleMessage)
        launchTimer()

        viewModelScope.launch {
            originalSchedule.collect {
                it.onReady {
                    it.onSuccess {
                        it.schedule?.let {
                            filterTypes.value = filterTypes.value.intersect(it.getAllTypes())
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            user.collect {
                Log.d(TAG, "5" + it.toString())
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

    suspend fun updateSchedule() {
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
        val times = (filteredSchedule.value as? ResultState.Ready)
            ?.result?.getOrNull()?.schedule
            ?.getLessons(LocalDate.now())?.map { it.time } ?: emptyList()
        currentLessonTimes.value = Pair(LessonTimeUtils.getCurrentTimes(LocalTime.now(), times), LocalTime.now())
    }


    fun setTodayDate() {
        date.value = LocalDate.now()
    }
}


