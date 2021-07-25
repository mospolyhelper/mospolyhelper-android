package com.mospolytech.mospolyhelper.domain.account.auth.usecase

import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.onStart

class AuthUseCase(
    private val repository: AuthRepository
) {
    suspend fun logIn(login: String, password: String) =
        repository.logIn(login, password).onStart {
            emit(Result0.Loading)
        }

    suspend fun refresh() = repository.refresh().onStart { emit(Result0.Loading) }

    fun logOut() {
        repository.logOut()
    }

    fun getName() = repository.getFio()

    fun getAvatar() = repository.getAvatar()

    fun getPermissions() = repository.getPermissions()
}