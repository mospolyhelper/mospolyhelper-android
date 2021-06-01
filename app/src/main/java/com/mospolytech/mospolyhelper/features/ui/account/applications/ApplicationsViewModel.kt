package com.mospolytech.mospolyhelper.features.ui.account.applications

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.applications.model.Application
import com.mospolytech.mospolyhelper.domain.account.applications.usecase.ApplicationsUseCase
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class ApplicationsViewModel (
    private val useCase: ApplicationsUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val applications = MutableStateFlow<Result2<List<Application>>>(Result2.loading())
    val auth = MutableStateFlow<Result2<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

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