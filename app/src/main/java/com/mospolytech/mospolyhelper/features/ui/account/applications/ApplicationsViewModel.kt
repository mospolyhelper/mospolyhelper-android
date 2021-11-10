package com.mospolytech.mospolyhelper.features.ui.account.applications

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.model.applications.Application
import com.mospolytech.mospolyhelper.domain.account.repository.ApplicationsRepository
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class ApplicationsViewModel (
    private val repository: ApplicationsRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val applications = MutableStateFlow<Result0<List<Application>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        applications.value = Result0.Loading
        repository.getApplications(emitLocal = false).collect {
            applications.value = it
        }
    }

    suspend fun getInfo() {
        repository.getApplications(emitLocal = true).collect {
            applications.value = it
        }
    }

}