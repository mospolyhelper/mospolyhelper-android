package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.usecase.DeadlinesUseCase
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class DeadlinesViewModel(
    private val useCase: DeadlinesUseCase,
    private val authUseCase: AuthUseCase,
    ) : ViewModel(), KoinComponent {

    val deadlines = MutableStateFlow<Result<List<Deadline>>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    val deadline: MutableLiveData<Deadline> by lazy {
        MutableLiveData()
    }

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            deadlines.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            deadlines.value = it
        }
        useCase.getInfo().collect {
            deadlines.value = it
        }
    }

    suspend fun setInfo(deadlinesList: List<Deadline>) {
        useCase.setInfo(deadlinesList).collect {
            deadlines.value = it
        }
    }
}