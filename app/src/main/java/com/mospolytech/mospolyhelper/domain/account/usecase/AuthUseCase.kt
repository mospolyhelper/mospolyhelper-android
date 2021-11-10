package com.mospolytech.mospolyhelper.domain.account.usecase

import com.mospolytech.mospolyhelper.domain.account.repository.AuthRepository
import com.mospolytech.mospolyhelper.utils.getAvatar
import com.mospolytech.mospolyhelper.utils.getName
import com.mospolytech.mospolyhelper.utils.getPermissions

class AuthUseCase(
    private val repository: AuthRepository
) {
    suspend fun logIn(login: String, password: String) = repository.logIn(login, password)

    suspend fun refresh() = repository.refresh()

    fun logOut() = repository.logOut()

    fun getName() = repository.getJWT()?.getName()

    fun getAvatar() = repository.getJWT()?.getAvatar()

    fun getPermissions() = repository.getJWT()?.getPermissions() ?: emptyList()
}