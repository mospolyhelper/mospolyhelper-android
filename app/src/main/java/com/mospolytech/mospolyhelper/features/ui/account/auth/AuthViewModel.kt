package com.mospolytech.mospolyhelper.features.ui.account.auth

import androidx.lifecycle.viewModelScope
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.features.ui.common.Mediator
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelBase
import com.mospolytech.mospolyhelper.features.ui.common.ViewModelMessage
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import org.koin.core.component.KoinComponent

class AuthViewModel(
    mediator: Mediator<String, ViewModelMessage>,
    private val useCase: AuthUseCase
) : ViewModelBase(mediator, AuthViewModel::class.java.simpleName), KoinComponent {

    val login: MutableStateFlow<String>
    val password: MutableStateFlow<String>
    val saveLogin: MutableStateFlow<Boolean>
    val savePassword: MutableStateFlow<Boolean>

    init {

        login = MutableStateFlow(useCase.getLogin())
        password = MutableStateFlow(useCase.getPassword())
        saveLogin = MutableStateFlow(useCase.getSaveLogin())
        savePassword = MutableStateFlow(useCase.getSavePassword())


        viewModelScope.async {
            login.collect {
                if (saveLogin.value) {
                    useCase.setLogin(it)
                }
            }
        }
        viewModelScope.async {
            password.collect {
                if (savePassword.value) {
                    useCase.setPassword(it)
                }
            }
        }
        viewModelScope.async {
            saveLogin.collect {
                useCase.setSaveLogin(it)
                if (it) {
                    useCase.setLogin(login.value)
                } else {
                    useCase.setLogin("")
                }
            }
        }
        viewModelScope.async {
            savePassword.collect {
                useCase.setSavePassword(it)
                if (it) {
                    useCase.setPassword(password.value)
                } else {
                    useCase.setPassword("")
                }
            }
        }
    }

    suspend fun logIn(): Flow<Result2<String>> {
        return useCase.logIn(login.value, password.value)
    }

    fun logOut() {
        useCase.logOut()
    }
}