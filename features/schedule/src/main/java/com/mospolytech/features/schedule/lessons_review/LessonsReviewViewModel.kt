package com.mospolytech.features.schedule.lessons_review

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.LessonTimesReview
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LessonsReviewViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModel<LessonsReviewState, LessonsReviewMutator>(
    LessonsReviewState(),
    LessonsReviewMutator()
) {

    init {
        viewModelScope.launch {
            useCase.getLessonsReview().collect {
                mutateState {
                    setLessons(it.getOrDefault(emptyList()))
                }
            }
        }
    }
}

data class LessonsReviewState(
    val lessons: List<LessonTimesReview> = emptyList()
)

class LessonsReviewMutator : BaseMutator<LessonsReviewState>() {
    fun setLessons(lessons: List<LessonTimesReview>) {
        if (state.lessons != lessons) {
            state = state.copy(lessons = lessons)
        }
    }
}