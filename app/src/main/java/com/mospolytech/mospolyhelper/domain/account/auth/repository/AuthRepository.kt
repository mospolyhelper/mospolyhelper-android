package com.mospolytech.mospolyhelper.domain.account.auth.repository

import com.mospolytech.mospolyhelper.utils.Result2
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun logIn(login: String, password: String): Flow<Result2<String>>
    suspend fun refresh(): Flow<Result2<String>>

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