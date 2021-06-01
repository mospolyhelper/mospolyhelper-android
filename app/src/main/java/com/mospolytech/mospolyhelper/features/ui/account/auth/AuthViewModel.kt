package com.mospolytech.mospolyhelper.features.ui.account.auth

import androidx.lifecycle.ViewModel
import com.mospolytech.mospolyhelper.domain.account.auth.usecase.AuthUseCase
import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent

class AuthViewModel(
    private val useCase: AuthUseCase
    ) : ViewModel(), KoinComponent {

    suspend fun logIn(login: String, password: String): Flow<Result2<String>> {
        return useCase.logIn(login, password)
    }

    fun logOut() {
        useCase.logOut()
    }

    fun getName() = useCase.getName()

    fun getAvatar() = useCase.getAvatar()
}