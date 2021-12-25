package com.mospolytech.features.account.classmates

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class ClassmatesViewModel(private val repository: PeoplesRepository) :
    BaseViewModel<ClassmatesState, ClassmatesMutator, Nothing>(ClassmatesState(), ClassmatesMutator()) {
        init {
            viewModelScope.launch {
                repository.getClassmates("ФИО").execute(
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

data class ClassmatesState(
    val data: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class ClassmatesMutator : BaseMutator<ClassmatesState>() {
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