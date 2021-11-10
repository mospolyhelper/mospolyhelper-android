package com.mospolytech.mospolyhelper.features.ui.account.classmates

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.classmates.Classmate
import com.mospolytech.mospolyhelper.domain.account.repository.ClassmatesRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class ClassmatesViewModel(
    private val repository: ClassmatesRepository,
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
        repository.getClassmates(emitLocal = false).collect {
            classmates.value = it
        }
    }

    suspend fun getInfo() {
        repository.getClassmates(emitLocal = true).collect {
            classmates.value = it
        }
    }

}