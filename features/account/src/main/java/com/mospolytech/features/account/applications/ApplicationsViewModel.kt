package com.mospolytech.features.account.applications

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.features.base.BaseMutator
import com.mospolytech.features.base.BaseViewModel
import com.mospolytech.features.base.utils.execute
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ApplicationsViewModel(private val repository: ApplicationsRepository) :
    BaseViewModel<ApplicationsState, ApplicationsMutator, Nothing>(ApplicationsState(), ::ApplicationsMutator) {

        init {
            loadData()
        }

    fun loadData() {
        viewModelScope.launch {
            if (!state.value.isLoading) {
                repository.getApplications().execute(
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

}

data class ApplicationsState(
    val data: List<Application?> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class ApplicationsMutator : BaseMutator<ApplicationsState>() {
    fun setData(data: List<Application>) {
        state = state.copy(data = data, isLoading = false, isError = false)
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError,
        data = if (state.data.isEmpty() && isLoading) List(5) { null } else state.data)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}