package com.mospolytech.mospolyhelper.features.ui.account.info

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.model.info.Info
import com.mospolytech.mospolyhelper.domain.account.repository.InfoRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class InfoViewModel(
    private val repository: InfoRepository,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val info = MutableStateFlow<Result0<Info>>(Result0.Loading)
    val auth = MutableStateFlow<Result0<String>?>(null)

    suspend fun downloadInfo() {
        repository.getInfo(emitLocal = false).collect {
            info.value = it
        }
    }

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun getInfo() {
        repository.getInfo(emitLocal = true).collect {
            info.value = it
        }
    }

    fun getAvatar() = authUseCase.getAvatar()

}