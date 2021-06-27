package com.mospolytech.mospolyhelper.features.ui.account.classmates

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.classmates.usecase.ClassmatesUseCase
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class ClassmatesViewModel(
    private val useCase: ClassmatesUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val classmates = MutableStateFlow<Result0<List<Classmate>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            classmates.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            classmates.value = it
        }
        useCase.getInfo().collect {
            classmates.value = it
        }
    }

}