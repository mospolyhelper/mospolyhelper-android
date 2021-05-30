package com.mospolytech.mospolyhelper.features.ui.account.auth

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class AuthViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: AuthUseCase
) : ViewModelBase(mediator, AuthViewModel::class.java.simpleName), KoinComponent {

    suspend fun logIn(login: String, password: String): Flow<Result<String>> {
        return useCase.logIn(login, password)
    }

    fun logOut() {
        useCase.logOut()
    }

    fun getName() = useCase.getName()

    fun getAvatar() = useCase.getAvatar()
}