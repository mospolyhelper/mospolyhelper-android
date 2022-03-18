package com.mospolytech.features.schedule.lessons_review

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.review.LessonTimesReview
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.core.mvi.BaseMutator
import com.mospolytech.features.base.core.mvi.BaseViewModelFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LessonsReviewViewModel(
    private val useCase: ScheduleUseCase
) : BaseViewModelFull<LessonsReviewState, LessonsReviewMutator, Nothing>(
    LessonsReviewState(),
    ::LessonsReviewMutator
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