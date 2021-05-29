package com.mospolytech.mospolyhelper.features.ui.account.deadlines

import androidx.lifecycle.MutableLiveData
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.usecase.ApplicationsUseCase
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.deadlines.usecase.DeadlinesUseCase
import com.mospolytech.mospolyhelper.features.ui.account.applications.ApplicationsViewModel
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.KoinComponent

class DeadlinesViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: DeadlinesUseCase,
    private val authUseCase: AuthUseCase,
) : ViewModelBase(mediator, DeadlinesViewModel::class.java.simpleName), KoinComponent {

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