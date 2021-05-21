package com.mospolytech.mospolyhelper.features.ui.account.applications

import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.usecase.ApplicationsUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class ApplicationsViewModel (
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: ApplicationsUseCase
) : ViewModelBase(mediator, ApplicationsViewModel::class.java.simpleName), KoinComponent {

    val applications = MutableStateFlow<Result<List<Application>>>(Result.loading())

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            applications.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            applications.value = it
        }
        useCase.getInfo().collect {
            applications.value = it
        }
    }

}