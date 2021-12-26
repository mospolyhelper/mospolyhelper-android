package com.mospolytech.features.account.students

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.launch

class StudentsViewModel(private val repository: PeoplesRepository) :
    BaseViewModel<StudentsState, StudentsMutator, Nothing>(StudentsState(), StudentsMutator()) {

        init {
            loadStudents()
        }

    fun loadStudents() {
        viewModelScope.launch {
            repository.getStudents("ФИО").execute(
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

    fun inputName(name: String) {

    }

}

data class StudentsState(
    val data: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class StudentsMutator : BaseMutator<StudentsState>() {
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