package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleLabelDeadline
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.mospolyhelper.domain.schedule.utils.filter
import com.mospolytech.mospolyhelper.domain.schedule.utils.getAllTypes
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.features.ui.schedule.lesson_info.LessonInfoViewModel
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.KoinComponent
import java.time.LocalDate
import java.time.LocalTime


class ScheduleViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ScheduleUseCase
) : ViewModelBase(mediator, ScheduleViewModel::class.java.simpleName), KoinComponent {

    companion object {
        const val MessageChangeDate = "ChangeDate"
        const val MessageSetAdvancedSearchSchedule = "SetAdvancedSearchSchedule"
        const val MessageAddScheduleId = "AddScheduleId"
    }

    var isAdvancedSearch = false
    private var firstLoading = true

    val date = MutableStateFlow(LocalDate.now())
    val currentLessonOrder: MutableStateFlow<Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>>


    val savedIds: MutableStateFlow<Set<Pair<Boolean, String>>>

    val showEndedLessons: MutableStateFlow<Boolean>
    val showCurrentLessons: MutableStateFlow<Boolean>
    val showNotStartedLessons: MutableStateFlow<Boolean>
    val filterTypes: MutableStateFlow<Set<String>>
    val showImportantLessons: MutableStateFlow<Boolean>
    val showAverageLessons: MutableStateFlow<Boolean>
    val showNotImportantLessons: MutableStateFlow<Boolean>
    val showNotLabeledLessons: MutableStateFlow<Boolean>



    val filteredSchedule = MutableStateFlow<Result<ScheduleLabelDeadline>>(Result.loading())

    val originalSchedule =  MutableStateFlow<Result<ScheduleLabelDeadline>>(Result.loading())
    val id: MutableStateFlow<Pair<Boolean, String>>
    val showEmptyLessons: MutableStateFlow<Boolean>

    val onMessage: Event1<String> = Action1()

    init {
        subscribe(::handleMessage)
        launchTimer()

        currentLessonOrder = MutableStateFlow(
            Pair(
                Lesson.getOrder(LocalTime.now(), false),
                Lesson.getOrder(LocalTime.now(), true)
            )
        )

        showEndedLessons = MutableStateFlow(useCase.getShowEndedLessons())
        showCurrentLessons = MutableStateFlow(useCase.getShowCurrentLessons())
        showNotStartedLessons = MutableStateFlow(useCase.getShowNotStartedLessons())
        filterTypes = MutableStateFlow(useCase.getFilterTypes())
        showImportantLessons = MutableStateFlow(useCase.getShowImportantLessons())
        showAverageLessons = MutableStateFlow(useCase.getShowAverageLessons())
        showNotImportantLessons = MutableStateFlow(useCase.getShowNotImportantLessons())
        showNotLabeledLessons = MutableStateFlow(useCase.getShowNotLabeledLessons())

        viewModelScope.async {
            showEndedLessons.collect {
                useCase.setShowEndedLessons(it)
            }
        }

        viewModelScope.async {
            showCurrentLessons.collect {
                useCase.setShowCurrentLessons(it)
            }
        }

        viewModelScope.async {
            showNotStartedLessons.collect {
                useCase.setShowNotStartedLessons(it)
            }
        }

        viewModelScope.async {
            filterTypes.collect { filters ->
                useCase.setFilterTypes(filters)
                originalSchedule.value.onSuccess {
                    filteredSchedule.value = Result.success(it.copy(schedule = it.schedule?.filter(types = filters)))
                }
            }
        }

        viewModelScope.async {
            showImportantLessons.collect {
                useCase.setShowImportantLessons(it)
            }
        }

        viewModelScope.async {
            showAverageLessons.collect {
                useCase.setShowAverageLessons(it)
            }
        }

        viewModelScope.async {
            showNotImportantLessons.collect {
                useCase.setShowNotImportantLessons(it)
            }
        }

        viewModelScope.async {
            showNotLabeledLessons.collect {
                useCase.setShowNotLabeledLessons(it)
            }
        }

        savedIds = MutableStateFlow(
            useCase.getSavedIds()
        )

        viewModelScope.async {
            savedIds.collect {
                useCase.setSavedIds(it)
            }
        }


        id = MutableStateFlow(
            Pair(
            useCase.getIsStudent(),
            useCase.getSelectedSavedId()
            )
        )

        showEmptyLessons = MutableStateFlow(useCase.getShowEmptyLessons())

        viewModelScope.async {
            id.collect {
                isAdvancedSearch = false
                useCase.setIsStudent(it.first)
                useCase.setSelectedSavedId(it.second)
                setUpSchedule(it.first, it.second, !firstLoading)
                firstLoading = false
            }
        }

        viewModelScope.async {
            showEmptyLessons.collect {
                useCase.setShowEmptyLessons(it)
            }
        }

        viewModelScope.async {
            savedIds.value = useCase.getSavedIds()
        }
        viewModelScope.async {
            originalSchedule.collect { result ->
                result.onSuccess {
                    filteredSchedule.value =
                        Result.success(
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
                    filteredSchedule.value = Result.loading()
                }
            }
        }
        if (savedIds.value.isEmpty() && id.value.second.isNotEmpty()) {
            id.value = Pair(false, "")
        }
    }


    private fun handleMessage(message: ViewModelMessage) {
        when (message.key) {
            MessageChangeDate -> {
                date.value = message.content[0] as LocalDate
            }
            MessageSetAdvancedSearchSchedule -> {
                isAdvancedSearch = true
                val sch = message.content[0]
                if (sch == null) {
                    originalSchedule.value = Result.loading()
                } else {
                    originalSchedule.value = Result.success(ScheduleLabelDeadline(sch as Schedule, emptyMap(), emptyMap()))
                }
            }
            MessageAddScheduleId -> {
                val pair = message.content[0] as Pair<Boolean, String>
                savedIds.value += pair
            }
        }
    }

    fun updateSchedule() {
        isAdvancedSearch = false
        setUpSchedule(
            id.value.first,
            id.value.second,
            true
        )
    }

    fun removeId(pair: Pair<Boolean, String>) {
        savedIds.value -= pair
        if (id.value.first == pair.first
            && pair.second.contains(id.value.second)
        ) {
            id.value = Pair(true, "")
        }
    }

    private fun launchTimer() {
        viewModelScope.async {
            while (isActive) {
                viewModelScope.async {
                    currentLessonOrder.value = Pair(
                        Lesson.getOrder(LocalTime.now(), false),
                        Lesson.getOrder(LocalTime.now(), true)
                    )
                }
                delay((60L - LocalTime.now().second) * 1000L)
            }
        }
    }


    private fun setUpSchedule(isStudent: Boolean, id: String, refresh: Boolean) {
        viewModelScope.async {
            this@ScheduleViewModel.originalSchedule.value = Result.loading()
            useCase.getScheduleWithFeatures(
                id,
                isStudent,
                refresh
            ).collect {
                this@ScheduleViewModel.originalSchedule.value = Result.success(it)
            }
        }
    }

    fun goHome() {
        date.value = LocalDate.now()
    }

    fun openLessonInfo(lesson: Lesson, date: LocalDate) {
        send(
            LessonInfoViewModel::class.java.simpleName,
            LessonInfoViewModel.LessonInfo,
            lesson,
            date
        )
    }
}


