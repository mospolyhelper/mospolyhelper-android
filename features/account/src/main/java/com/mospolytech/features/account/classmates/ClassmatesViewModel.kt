package com.mospolytech.features.account.classmates

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Student
import com.mospolytech.domain.account.repository.PeoplesRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import com.mospolytech.features.base.utils.isNull
import kotlinx.coroutines.launch

class ClassmatesViewModel(private val repository: PeoplesRepository) :
    BaseViewModel<ClassmatesState, ClassmatesMutator, Nothing>(ClassmatesState(), ::ClassmatesMutator) {
        init {
            loadClassmates()
        }

    fun loadClassmates() {
        viewModelScope.launch {
            if (!state.value.isLoading) {
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

    fun inputName(name: String) {
        mutateState {
            setName(name)
        }
    }

}

data class ClassmatesState(
    val data: List<Student?> = emptyList(),
    val name: String = "",
    val filteredData: List<Student?> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class ClassmatesMutator : BaseMutator<ClassmatesState>() {
    fun setName(name: String) {
        state = state.copy(name = name, filteredData = state.data.filter {
            val predicate = it?.name?.contains(name, true)
            predicate == true || predicate.isNull()
        })
    }

    fun setData(data: List<Student>) {
        state = state.copy(data = data, isLoading = false, isError = false, filteredData = data.filter {
            it.name.contains(state.name, true)
        })
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError,
            data = if (state.data.isEmpty() && isLoading) List(5) { null } else state.data)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}