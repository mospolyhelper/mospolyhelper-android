package com.mospolytech.features.account.marks

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.model.Marks
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.domain.account.repository.PerformanceRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class MarksViewModel(private val repository: PerformanceRepository) :
    BaseViewModel<MarksState, MarksMutator>(MarksState(), MarksMutator()) {

        init {
            viewModelScope.launch {
                repository.getMarks().execute(
                    onStart = {
                        mutateState {
                            setLoading(true)
                        }
                    },
                    onSuccess = {
                        mutateState {
                            setData(it)
                        }
                    },
                    onError = {
                        mutateState {
                            setError(true)
                        }
                    }
                )
            }
        }

}

data class MarksState(
    val data: List<Marks> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class MarksMutator : BaseMutator<MarksState>() {
    fun setData(data: List<Marks>) {
        state = state.copy(data = data, isLoading = false, isError = false)
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}