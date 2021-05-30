package com.mospolytech.mospolyhelper.domain.account.auth.repository

import kotlinx.coroutines.flow.Flow
import com.mospolytech.mospolyhelper.utils.Result

interface AuthRepository {
    suspend fun logIn(login: String, password: String): Flow<Result<String>>
    suspend fun refresh(): Flow<Result<String>>

    fun getAvatar(): String?
    fun getPermissions(): List<String>
    fun getFio(): String?

    fun logOut()

    fun getLogin(): String
    fun setLogin(value: String)

    fun getPassword(): String
    fun setPassword(value: String)

    fun getSaveLogin(): Boolean
    fun setSaveLogin(value: Boolean)

    fun getSavePassword(): Boolean
    fun setSavePassword(value: Boolean)
}