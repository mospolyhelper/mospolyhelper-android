package com.mospolytech.mospolyhelper.data.account.auth.repository

import com.mospolytech.mospolyhelper.data.account.auth.remote.AuthRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.utils.PreferenceDefaults
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.onSuccess
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val dataSource: AuthRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource
) : AuthRepository {
    override suspend fun logIn(login: String, password: String) = flow {
        val oldSessionId = prefDataSource.getString(PreferenceKeys.SessionId, PreferenceDefaults.SessionId)
        val newSessionId = dataSource.auth(login, password, oldSessionId)
        newSessionId.onSuccess {
            prefDataSource.setString(PreferenceKeys.SessionId, it)
        }
        emit(newSessionId)
    }

    override fun logOut() {
        prefDataSource.setString(PreferenceKeys.SessionId, PreferenceDefaults.SessionId)
        prefDataSource.setString(PreferenceKeys.Info, "")
    }

    override fun getLogin(): String {
        return prefDataSource.getString(PreferenceKeys.Login, PreferenceDefaults.Login)
    }

    override fun setLogin(value: String) {
        prefDataSource.setString(PreferenceKeys.Login, value)
    }

    override fun getPassword(): String {
        return prefDataSource.getString(PreferenceKeys.Password, PreferenceDefaults.Password)
    }

    override fun setPassword(value: String) {
        prefDataSource.setString(PreferenceKeys.Password, value)
    }

    override fun getSaveLogin(): Boolean {
        return prefDataSource.getBoolean(PreferenceKeys.SaveLogin, PreferenceDefaults.SaveLogin)
    }

    override fun setSaveLogin(value: Boolean) {
        prefDataSource.setBoolean(PreferenceKeys.SaveLogin, value)
    }

    override fun getSavePassword(): Boolean {
        return prefDataSource.getBoolean(PreferenceKeys.SavePassword, PreferenceDefaults.SavePassword)
    }

    override fun setSavePassword(value: Boolean) {
        prefDataSource.setBoolean(PreferenceKeys.SavePassword, value)
    }
}