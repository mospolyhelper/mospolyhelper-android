package com.mospolytech.features.account.teachers

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.model.Teacher
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class TeachersViewModel(private val repository: PeoplesRepository) :
    BaseViewModel<TeachersState, TeachersMutator, Nothing>(TeachersState(), TeachersMutator()) {
        init {
            load()
        }

    fun load() {
        viewModelScope.launch {
            repository.getTeachers("ФИО").execute(
                onStart = {
                    mutateState {
                        setLoading(true)
                    }
                },
                onSuccess = {
                    mutateState {
                        setData(it.data)
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

data class TeachersState(
    val data: List<Teacher> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class TeachersMutator : BaseMutator<TeachersState>() {
    fun setData(data: List<Teacher>) {
        state = state.copy(data = data, isLoading = false, isError = false)
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}