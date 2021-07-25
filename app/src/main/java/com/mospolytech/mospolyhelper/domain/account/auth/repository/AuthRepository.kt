package com.mospolytech.mospolyhelper.domain.account.auth.repository

import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun logIn(login: String, password: String): Flow<Result0<String>>
    suspend fun refresh(): Flow<Result0<String>>

    fun getAvatar(): String?
    fun getPermissions(): List<String>
    fun getFio(): String?

    fun logOut()
}