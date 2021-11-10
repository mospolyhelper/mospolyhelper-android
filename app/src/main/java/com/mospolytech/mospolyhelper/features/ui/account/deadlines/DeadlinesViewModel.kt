package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.Deadline
import com.mospolytech.mospolyhelper.domain.account.repository.DeadlinesRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class DeadlinesViewModel(
    private val repository: DeadlinesRepository,
    private val authUseCase: AuthUseCase,
    ) : ViewModel(), KoinComponent {

    val deadlines = MutableStateFlow<Result0<List<Deadline>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    val deadline: MutableLiveData<Deadline> by lazy {
        MutableLiveData()
    }

    suspend fun downloadInfo() {
        repository.getDeadlines(emitLocal = false).collect {
            deadlines.value = it
        }
    }

    suspend fun getInfo() {
        repository.getDeadlines(emitLocal = true).collect {
            deadlines.value = it
        }
    }

    suspend fun setInfo(deadlinesList: List<Deadline>) {
        repository.setDeadlines(deadlinesList).collect {
            deadlines.value = it
        }
    }
}