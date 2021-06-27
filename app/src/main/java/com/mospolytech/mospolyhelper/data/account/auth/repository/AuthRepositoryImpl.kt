package com.mospolytech.mospolyhelper.data.account.auth.repository

import android.app.Application
import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.account.auth.local.AuthLocalDataSource
import com.mospolytech.mospolyhelper.data.account.auth.remote.AuthRemoteDataSource
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.clearJson
import com.mospolytech.mospolyhelper.data.utils.getFromJson
import com.mospolytech.mospolyhelper.domain.account.auth.repository.AuthRepository
import com.mospolytech.mospolyhelper.domain.account.classmates.model.Classmate
import com.mospolytech.mospolyhelper.domain.account.deadlines.model.Deadline
import com.mospolytech.mospolyhelper.domain.account.dialogs.model.DialogModel
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.domain.account.marks.model.Marks
import com.mospolytech.mospolyhelper.domain.account.payments.model.Payments
import com.mospolytech.mospolyhelper.domain.account.statements.model.Statements
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val dataSourceJWT: AuthRemoteDataSource,
    private val prefDataSource: SharedPreferencesDataSource,
    private val localDataSourceJWT: AuthLocalDataSource
) : AuthRepository {

    override suspend fun logIn(login: String, password: String) = flow {
        val token = dataSourceJWT.authJwt(login, password)
        emit(token.map {
            localDataSourceJWT.accessToken = it.accessToken
            val sessionId = localDataSourceJWT.jwt?.getSessionId()!!
            prefDataSource.set(PreferenceKeys.SessionId, sessionId)
            prefDataSource.set(PreferenceKeys.RefreshToken, it.refreshToken)
            return@map sessionId
        })
    }

    override suspend fun refresh(): Flow<Result0<String>> = flow {
        if (prefDataSource.getFromJson<JWT>()?.isExpired() == true) {
            val oldToken = prefDataSource.get(PreferenceKeys.AccessToken, "")
            val refresh = prefDataSource.get(PreferenceKeys.RefreshToken, "")
            val newToken = dataSourceJWT.refresh(oldToken, refresh)
            newToken.onSuccess {
                localDataSourceJWT.accessToken = it.replace("\"", "")
                val sessionId = localDataSourceJWT.jwt?.getSessionId()!!
                prefDataSource.set(PreferenceKeys.SessionId, sessionId)
            }
            emit(newToken)
        }
    }

    override fun getAvatar() =
        localDataSourceJWT.jwt?.getAvatar()

    override fun getPermissions() =
        localDataSourceJWT.jwt?.getPermissions() ?: emptyList()

    override fun getFio() =
        localDataSourceJWT.jwt?.getName()

    override fun logOut() {
        localDataSourceJWT.clearToken()
        prefDataSource.set(PreferenceKeys.SessionId, PreferenceDefaults.SessionId)
        prefDataSource.set(PreferenceKeys.RefreshToken, "")
        prefDataSource.clearJson<List<Application>>(PreferenceKeys.Applications)
        prefDataSource.clearJson<List<Classmate>>(PreferenceKeys.Classmates)
        prefDataSource.clearJson<List<Deadline>>(PreferenceKeys.Deadlines)
        prefDataSource.clearJson<Info>()
        prefDataSource.clearJson<Marks>()
        prefDataSource.clearJson<Payments>()
        prefDataSource.clearJson<List<DialogModel>>(PreferenceKeys.Dialogs)
        prefDataSource.clearJson<Statements>()
    }

}