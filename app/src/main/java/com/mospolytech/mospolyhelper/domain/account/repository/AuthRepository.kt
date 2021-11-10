package com.mospolytech.mospolyhelper.domain.account.repository

import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.utils.Result0
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun logIn(login: String, password: String): Flow<Result0<String>>
    suspend fun refresh(): Flow<Result0<String>>

    fun getJWT(): JWT?

    fun logOut()
}