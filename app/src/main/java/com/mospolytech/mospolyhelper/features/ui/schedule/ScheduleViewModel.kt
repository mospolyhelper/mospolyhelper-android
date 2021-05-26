package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.*
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleTagsDeadline
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
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


class ScheduleViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModelBase(mediator, ScheduleViewModel::class.java.simpleName), KoinComponent {

    companion object {
        const val MessageChangeDate = "ChangeDate"
        const val MessageSetAdvancedSearchSchedule = "SetAdvancedSearchSchedule"
        const val MessageAddScheduleId = "AddScheduleId"
    }
    val date = MutableStateFlow(LocalDate.now())
    //val currentLessonOrder: MutableStateFlow<Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>>
    val savedIds: MutableStateFlow<Set<UserSchedule>>
    val showEndedLessons: MutableStateFlow<Boolean>
    val showCurrentLessons: MutableStateFlow<Boolean>
    val showNotStartedLessons: MutableStateFlow<Boolean>
    val filterTypes: MutableStateFlow<Set<String>>
    val showImportantLessons: MutableStateFlow<Boolean>
    val showAverageLessons: MutableStateFlow<Boolean>
    val showNotImportantLessons: MutableStateFlow<Boolean>


    val filteredSchedule = MutableStateFlow<Result2<ScheduleTagsDeadline>>(Result2.loading())

    val originalSchedule =  MutableStateFlow<Result2<ScheduleTagsDeadline>>(Result2.loading())
    val user: MutableStateFlow<UserSchedule?>
    val showEmptyLessons: MutableStateFlow<Boolean>

    val onMessage: Event1<String> = Action1()

    init {
        subscribe(::handleMessage)
        launchTimer()

        showEndedLessons = MutableStateFlow(useCase.getShowEndedLessons())
        showCurrentLessons = MutableStateFlow(useCase.getShowCurrentLessons())
        showNotStartedLessons = MutableStateFlow(useCase.getShowNotStartedLessons())
        filterTypes = MutableStateFlow(useCase.getFilterTypes())
        showImportantLessons = MutableStateFlow(PreferenceDefaults.ShowImportantLessons)
        showAverageLessons = MutableStateFlow(PreferenceDefaults.ShowAverageLessons)
        showNotImportantLessons = MutableStateFlow(PreferenceDefaults.ShowNotImportantLessons)

        viewModelScope.launch {
            showEndedLessons.collect {
                useCase.setShowEndedLessons(it)
            }
        }

        viewModelScope.launch {
            showCurrentLessons.collect {
                useCase.setShowCurrentLessons(it)
            }
        }

        viewModelScope.launch {
            showNotStartedLessons.collect {
                useCase.setShowNotStartedLessons(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            filterTypes.collect { filters ->
                useCase.setFilterTypes(filters)
                originalSchedule.value.onSuccess {
                    filteredSchedule.value = Result2.success(it.copy(schedule = it.schedule?.filter(types = filters)))
                }
            }
        }

        savedIds = MutableStateFlow(
            useCase.getSavedIds()
        )

        viewModelScope.launch {
            savedIds.collect {
                useCase.setSavedIds(it)
            }
        }


        user = MutableStateFlow(
            useCase.getUserSchedule()
        )

        showEmptyLessons = MutableStateFlow(useCase.getShowEmptyLessons())

        viewModelScope.launch {
            user.collect {
                if (it !is AdvancedSearchSchedule) {
                    useCase.setSelectedSavedId(it)
                }
                setUpSchedule(it)
            }
        }

        viewModelScope.launch {
            showEmptyLessons.collect {
                useCase.setShowEmptyLessons(it)
            }
        }

        viewModelScope.launch {
            savedIds.value = useCase.getSavedIds()
        }
        viewModelScope.launch {
            originalSchedule.collect { result ->
                result.onSuccess {
                    filteredSchedule.value =
                        Result2.success(
                            it.copy(
                                schedule = it.schedule?.filter(types = filterTypes.value)
                            )
                        )
                    val schedule = it.schedule
                    if (schedule != null) {
                        filterTypes.value = filterTypes.value.intersect(schedule.getAllTypes())
                    }
                }
                result.onLoading {
                    filteredSchedule.value = Result2.loading()
                }
            }
        }
        if (savedIds.value.isEmpty() && user.value != null) {
            user.value = null
        }
    }


    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            MessageChangeDate -> {
                date.value = message.content[0] as LocalDate
            }
            MessageSetAdvancedSearchSchedule -> {
                user.value = AdvancedSearchSchedule(message.content[0] as ScheduleFilters)
            }
            MessageAddScheduleId -> {
                val pair = message.content[0] as UserSchedule
                savedIds.value += pair
            }
        }
    }

    suspend fun updateSchedule() {
        user.value = useCase.getUserSchedule()
        useCase.updateSchedule(user.value)
    }

    fun removeId(user: UserSchedule) {
        savedIds.value -= user
        val currUser = this.user.value
        if (currUser is StudentSchedule && user is StudentSchedule
            && user.title.contains(currUser.title)
        ) {
            this.user.value = null
        }
    }

    private fun launchTimer() {
//        viewModelScope.async {
//            while (isActive) {
//                viewModelScope.async {
//                    currentLessonOrder.value = Pair(
//                        LessonTimeUtils.getOrder(LocalTime.now(), false),
//                        LessonTimeUtils.getOrder(LocalTime.now(), true)
//                    )
//                }
//                delay((60L - LocalTime.now().second) * 1000L)
//            }
//        }
    }


    private fun setUpSchedule(user: UserSchedule?) {
        viewModelScope.launch {
            this@ScheduleViewModel.originalSchedule.value = Result2.loading()
            useCase.getScheduleWithFeatures(
                user
            ).collect {
                this@ScheduleViewModel.originalSchedule.value = Result2.success(it)
            }
        }
    }

    fun setTodayDate() {
        date.value = LocalDate.now()
    }
}


