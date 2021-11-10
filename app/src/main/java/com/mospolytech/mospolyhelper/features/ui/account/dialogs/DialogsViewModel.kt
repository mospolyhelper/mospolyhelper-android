package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.dialogs.DialogModel
import com.mospolytech.mospolyhelper.domain.account.repository.DialogsRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class DialogsViewModel(
    private val repository: DialogsRepository,
    private val authUseCase: AuthUseCase
    ): ViewModel(), KoinComponent {

    val dialogs = MutableStateFlow<Result0<List<DialogModel>>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        repository.getDialogs(emitLocal = false).collect {
            dialogs.value = it
        }
    }

    suspend fun getInfo() {
        repository.getDialogs(emitLocal = true).collect {
            dialogs.value = it
        }
    }
}