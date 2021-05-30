package com.mospolytech.mospolyhelper.features.ui.account.dialogs

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.dialogs.usecase.DialogsUseCase
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class DialogsViewModel(
    private val useCase: DialogsUseCase,
    private val authUseCase: AuthUseCase
    ): ViewModel(), KoinComponent {

    val dialogs = MutableStateFlow<Result<List<DialogModel>>>(Result.loading())
    val auth = MutableStateFlow<Result<String>?>(null)

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            dialogs.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            dialogs.value = it
        }

        useCase.getInfo().collect {
            dialogs.value = it
        }
    }
}