package com.mospolytech.features.schedule.menu

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.schedule.LessonsByTime
import com.mospolytech.domain.schedule.model.source.ScheduleSourceFull
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.domain.schedule.utils.getClosestLessons
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.navigation.ScheduleScreens
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ScheduleMenuViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<ScheduleMenuState, ScheduleMenuMutator, Nothing>(
    ScheduleMenuState(),
    ::ScheduleMenuMutator
) {
    init {
        viewModelScope.launch {
            useCase.getSchedule().collect {
                val lessons = it.getOrNull()?.let {
                    useCase.getScheduleDay(it, LocalDate.now())
                } ?: emptyList()

                val now = LocalTime.now()
                val closestLessons = getClosestLessons(lessons)
                    .map {
                        ClosestLessons(
                            now.until(it.time.startTime, ChronoUnit.MINUTES)
                                .toDuration(DurationUnit.MINUTES),
                            it
                        )
                    }.filter { now <= it.lessons.time.endTime }

                val (notStarted, current) = closestLessons
                    .groupBy { it.timeToStart.isNegative() }
                    .run {
                        getOrDefault(false, emptyList()) to
                                getOrDefault(true, emptyList())
                    }


            }
        }

        viewModelScope.launch {
            useCase.getSelectedSource().collect {
                mutateState { setSelectedSource(it.getOrNull()) }
            }
        }
    }

    fun onScheduleClick() {
        router.navigateTo(ScheduleScreens.Main)
    }

    fun onLessonsReviewClick() {
        router.navigateTo(ScheduleScreens.LessonsReview)
    }

    fun onScheduleCalendarClick() {
        router.navigateTo(ScheduleScreens.Calendar)
    }

    fun onScheduleSourceClick() {
        router.navigateTo(ScheduleScreens.Source)
    }

    fun onFreePlaceClick() {
        router.navigateTo(ScheduleScreens.FreePlace)
    }
}

data class ScheduleMenuState(
    val main: MainState = MainState(),
    val source: SourceState = SourceState(),
    val date: LocalDate = LocalDate.now()
) {
    data class MainState(
        val currentLessons: List<ClosestLessons> = emptyList(),
        val notStartedLessons: List<ClosestLessons> = emptyList()
    )

    data class SourceState(
        val selectedSource: ScheduleSourceFull? = null
    )
}

class ScheduleMenuMutator : BaseMutator<ScheduleMenuState>() {
    fun setSelectedSource(selectedSource: ScheduleSourceFull?) =
        set(state.source.selectedSource, selectedSource) {
            copy(source = source.copy(selectedSource = it))
        }
}

class ClosestLessons(
    val timeToStart: Duration,
    val lessons: LessonsByTime
)
