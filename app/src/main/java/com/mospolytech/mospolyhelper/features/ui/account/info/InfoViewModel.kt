package com.mospolytech.mospolyhelper.features.ui.account.info

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.info.usecase.InfoUseCase
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class InfoViewModel(
    private val useCase: InfoUseCase,
    private val authUseCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    val info = MutableStateFlow<Result2<Info>>(Result2.loading())
    val auth = MutableStateFlow<Result2<String>?>(null)

    suspend fun downloadInfo() {
        useCase.getInfo().collect {
            info.value = it
        }
    }

    suspend fun refresh() {
        authUseCase.refresh().collect {
            auth.value = it
        }
    }

    suspend fun getInfo() {
        useCase.getLocalInfo().collect {
            info.value = it
        }
        useCase.getInfo().collect {
            info.value = it
        }
    }

    fun getAvatar() = useCase.getAvatar()

}