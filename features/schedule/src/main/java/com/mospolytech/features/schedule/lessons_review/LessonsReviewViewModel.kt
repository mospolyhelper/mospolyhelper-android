package com.mospolytech.features.schedule.lessons_review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.schedule.model.LessonTimesReview
import com.mospolytech.domain.schedule.usecase.ScheduleUseCase
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.State
import com.mospolytech.features.base.mutate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LessonsReviewViewModel(
    private val useCase: ScheduleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LessonsReviewState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            useCase.getLessonsReview().collect {
                _state.value = _state.value.mutate {
                    setLessons(it.getOrDefault(emptyList()))
                }
            }
        }
    }
}

data class LessonsReviewState(
    val lessons: List<LessonTimesReview> = emptyList()

) : State<LessonsReviewState.Mutator> {
    inner class Mutator : BaseMutator<LessonsReviewState>(this) {
        fun setLessons(lessons: List<LessonTimesReview>) {
            if (state.lessons != lessons) {
                state = state.copy(lessons = lessons)
            }
        }
    }

    override fun mutator() = Mutator()
}