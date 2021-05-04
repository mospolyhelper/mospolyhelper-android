package com.mospolytech.mospolyhelper.domain.account.auth.usecase

import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.coroutines.flow.*

class AuthUseCase(
    private val repository: AuthRepository
) {
    suspend fun logIn(login: String, password: String) =
        repository.logIn(login, password).onStart {
            emit(Result.loading())
        }

    suspend fun refresh() = repository.refresh().onStart { emit(Result.loading()) }

    fun logOut() {
        repository.logOut()
    }

    fun getLogin(): String {
        return repository.getLogin()
    }

    fun setLogin(value: String ) {
        repository.setLogin(value)
    }

    fun getPassword(): String  {
        return repository.getPassword()
    }

    fun setPassword(value: String ) {
        repository.setPassword(value)
    }

    fun getSaveLogin(): Boolean {
        return repository.getSaveLogin()
    }

    fun setSaveLogin(value: Boolean) {
        repository.setSaveLogin(value)
    }

    fun getSavePassword(): Boolean {
        return repository.getSavePassword()
    }

    fun setSavePassword(value: Boolean) {
        repository.setSavePassword(value)
    }

    fun getName() = repository.getFio()

    fun getAvatar() = repository.getAvatar()

    fun getPermissions() = repository.getPermissions()
}