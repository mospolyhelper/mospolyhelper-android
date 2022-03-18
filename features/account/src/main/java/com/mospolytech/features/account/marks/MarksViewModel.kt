package com.mospolytech.features.account.marks

import androidx.lifecycle.viewModelScope
import com.mospolytech.domain.account.model.Application
import com.mospolytech.domain.account.model.Marks
import com.mospolytech.domain.account.repository.ApplicationsRepository
import com.mospolytech.domain.account.repository.PerformanceRepository
import com.mospolytech.features.base.core.BaseMutator
import com.mospolytech.features.base.core.BaseViewModel
import com.mospolytech.domain.base.utils.execute
import kotlinx.coroutines.launch

class MarksViewModel(private val repository: PerformanceRepository) :
    BaseViewModel<MarksState, MarksMutator, Nothing>(MarksState(), ::MarksMutator) {

        init {
            loadMarks()
        }

    fun loadMarks() {
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
    val coursesAndSemesters: Map<Int, Int> = emptyMap(),
    val semester: Int? = null,
    val filteredData: Marks? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class MarksMutator : BaseMutator<MarksState>() {
    fun setData(data: List<Marks>) {
        state = state.copy(data = data, isLoading = false, isError = false,
            coursesAndSemesters = data.map { it.semester to it.course }.toMap())
    }

    fun setSemester(semester: Int) {
        state = state.copy(semester = semester, filteredData = state.data.first { it.semester == semester })
    }

    fun setLoading(isLoading: Boolean) {
        state = state.copy(isLoading = isLoading, isError = !isLoading && state.isError)
    }

    fun setError(isError: Boolean) {
        state = state.copy(isError = isError, isLoading = false)
    }
}