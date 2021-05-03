package com.mospolytech.mospolyhelper.features.ui.schedule

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.schedule.model.Lesson
import com.mospolytech.mospolyhelper.domain.schedule.model.Schedule
import com.mospolytech.mospolyhelper.domain.schedule.model.StudentSchedule
import com.mospolytech.mospolyhelper.domain.schedule.model.UserSchedule
import com.mospolytech.mospolyhelper.domain.schedule.usecase.ScheduleTagsDeadline
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
    //val currentLessonOrder: MutableStateFlow<Pair<Lesson.CurrentLesson, Lesson.CurrentLesson>>


    val savedIds: MutableStateFlow<Set<UserSchedule>>

    val showEndedLessons: MutableStateFlow<Boolean>
    val showCurrentLessons: MutableStateFlow<Boolean>
    val showNotStartedLessons: MutableStateFlow<Boolean>
    val filterTypes: MutableStateFlow<Set<String>>
    val showImportantLessons: MutableStateFlow<Boolean>
    val showAverageLessons: MutableStateFlow<Boolean>
    val showNotImportantLessons: MutableStateFlow<Boolean>



    val filteredSchedule = MutableStateFlow<Result<ScheduleTagsDeadline>>(Result.loading())

    val originalSchedule =  MutableStateFlow<Result<ScheduleTagsDeadline>>(Result.loading())
    val id: MutableStateFlow<UserSchedule?>
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

        viewModelScope.async(Dispatchers.IO) {
            filterTypes.collect { filters ->
                useCase.setFilterTypes(filters)
                originalSchedule.value.onSuccess {
                    filteredSchedule.value = Result.success(it.copy(schedule = it.schedule?.filter(types = filters)))
                }
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
            useCase.getSelectedSavedId()
        )

        showEmptyLessons = MutableStateFlow(useCase.getShowEmptyLessons())

        viewModelScope.async {
            id.collect {
                isAdvancedSearch = false
                useCase.setSelectedSavedId(it)
                setUpSchedule(it, !firstLoading)
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
        if (savedIds.value.isEmpty() && id.value != null) {
            id.value = null
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
                    originalSchedule.value = Result.success(ScheduleTagsDeadline(sch as Schedule, emptyList(), emptyMap()))
                }
            }
            MessageAddScheduleId -> {
                val pair = message.content[0] as UserSchedule
                savedIds.value += pair
            }
        }
    }

    fun updateSchedule() {
        isAdvancedSearch = false
        setUpSchedule(
            id.value,
            true
        )
    }

    fun removeId(user: UserSchedule) {
        savedIds.value -= user
        val currUser = id.value
        if (currUser is StudentSchedule && user is StudentSchedule
            && user.title.contains(currUser.title)
        ) {
            id.value = null
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


    private fun setUpSchedule(user: UserSchedule?, refresh: Boolean) {
        viewModelScope.async {
            this@ScheduleViewModel.originalSchedule.value = Result.loading()
            useCase.getScheduleWithFeatures(
                user,
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


