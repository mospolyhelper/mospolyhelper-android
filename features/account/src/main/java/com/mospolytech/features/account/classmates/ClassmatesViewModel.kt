package com.mospolytech.features.account.classmates

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.account.base.DataLoadingState
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ClassmatesViewModel(private val repository: PeoplesRepository) :
    BaseViewModel<ApplicationsState, ApplicationsMutator>(ApplicationsState(), ApplicationsMutator()) {
        init {
            viewModelScope.launch {
                repository.getClassmates().execute(
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

data class ApplicationsState(
    val data: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class ApplicationsMutator : BaseMutator<ApplicationsState>() {
    fun setData(data: List<Student>) {
        state = state.copy(data = data, isLoading = false, isError = false)
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}