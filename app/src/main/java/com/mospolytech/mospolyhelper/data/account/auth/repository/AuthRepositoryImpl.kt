package com.mospolytech.mospolyhelper.data.account.auth.repository

import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.account.auth.local.AuthJwtLocalDataSource
import com.mospolytech.mospolyhelper.data.account.auth.remote.AuthJwtRemoteDataSource
import com.mospolytech.mospolyhelper.data.account.auth.remote.AuthRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.auth.model.JwtModel
import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.contracts.ExperimentalContracts

class AuthRepositoryImpl(
    private val dataSourceJWT: AuthJwtRemoteDataSource,
    private val authJwtLocalDataSource: AuthJwtLocalDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : AuthRepository {

    @ExperimentalContracts
    override suspend fun logIn(login: String, password: String) = flow {
        val token = dataSourceJWT.authJwt(login, password)
        emit(token.map {
            authJwtLocalDataSource.set(it.accessToken)
            val sessionId = authJwtLocalDataSource.get()?.getSessionId()!!
            prefDataSource.set(PreferenceKeys.SessionId, sessionId)
            prefDataSource.set(PreferenceKeys.RefreshToken, it.refreshToken)
            return@map sessionId
        })
    }

    @ExperimentalContracts
    override suspend fun refresh(): Flow<Result<String>> = flow {
        if (authJwtLocalDataSource.get()?.isExpired() == true) {
            val oldToken = prefDataSource.get(PreferenceKeys.AccessToken, "")
            val refresh = prefDataSource.get(PreferenceKeys.RefreshToken, "")
            val newToken = dataSourceJWT.refresh(oldToken, refresh)
            newToken.onSuccess {
                authJwtLocalDataSource.set(it.replace("\"", ""))
                val sessionId = authJwtLocalDataSource.get()?.getSessionId()!!
                prefDataSource.set(PreferenceKeys.SessionId, sessionId)
            }
//            val sessionId = authJwtLocalDataSource.get()?.getSessionId()!!
//            prefDataSource.set(PreferenceKeys.SessionId, sessionId)
//            emit(newToken.map {
//                authJwtLocalDataSource.set(it)
//                val sessionId = authJwtLocalDataSource.get()?.getSessionId()!!
//                prefDataSource.set(PreferenceKeys.SessionId, sessionId)
//                return@map sessionId
//            })
        }
    }

    override fun getAvatar() = authJwtLocalDataSource.get()?.getAvatar() ?: ""

    override fun getPermissions() = authJwtLocalDataSource.get()?.getPermissions() ?: emptyList()

    override fun getFio() = authJwtLocalDataSource.get()?.getName() ?: ""

    override fun logOut() {
        prefDataSource.set(PreferenceKeys.SessionId, PreferenceDefaults.SessionId)
        prefDataSource.set(PreferenceKeys.Info, "")
        authJwtLocalDataSource.clear()
    }

    override fun getLogin(): String {
        return prefDataSource.get(PreferenceKeys.Login, PreferenceDefaults.Login)
    }

    override fun setLogin(value: String) {
        prefDataSource.set(PreferenceKeys.Login, value)
    }

    override fun getPassword(): String {
        return prefDataSource.get(PreferenceKeys.Password, PreferenceDefaults.Password)
    }

    override fun setPassword(value: String) {
        prefDataSource.set(PreferenceKeys.Password, value)
    }

    override fun getSaveLogin(): Boolean {
        return prefDataSource.get(PreferenceKeys.SaveLogin, PreferenceDefaults.SaveLogin)
    }

    override fun setSaveLogin(value: Boolean) {
        prefDataSource.set(PreferenceKeys.SaveLogin, value)
    }

    override fun getSavePassword(): Boolean {
        return prefDataSource.get(PreferenceKeys.SavePassword, PreferenceDefaults.SavePassword)
    }

    override fun setSavePassword(value: Boolean) {
        prefDataSource.set(PreferenceKeys.SavePassword, value)
    }
}