package com.mospolytech.mospolyhelper.data.account.repository

import android.app.Application
import android.util.Log
import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.account.api.AccountApi
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.data.utils.clearObject
import com.mospolytech.mospolyhelper.domain.account.repository.AuthRepository
import com.mospolytech.mospolyhelper.domain.account.model.classmates.Classmate
import com.mospolytech.mospolyhelper.domain.account.model.deadlines.Deadline
import com.mospolytech.mospolyhelper.domain.account.model.dialogs.DialogModel
import com.mospolytech.mospolyhelper.domain.account.model.info.Info
import com.mospolytech.mospolyhelper.domain.account.model.marks.Marks
import com.mospolytech.mospolyhelper.domain.account.model.payments.Payments
import com.mospolytech.mospolyhelper.domain.account.model.statements.Statements
import com.mospolytech.mospolyhelper.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val api: AccountApi,
    private val prefDataSource: SharedPreferencesDataSource
) : AuthRepository {

    override suspend fun logIn(login: String, password: String) = flow {
        emit(Result0.Loading)
        val token = api.auth(login, password)
        val sessionIdResult = token.map {
            prefDataSource.set(PreferenceKeys.AccessToken, it.accessToken)
            prefDataSource.set(PreferenceKeys.RefreshToken, it.refreshToken)
            val jwt = JWT(it.accessToken)
            val sessionId = jwt.getSessionId() ?: PreferenceDefaults.SessionId
            prefDataSource.set(PreferenceKeys.SessionId, sessionId)
            return@map sessionId
        }
        emit(sessionIdResult)
    }

    override suspend fun refresh(): Flow<Result0<String>> = flow {
        val accessToken = prefDataSource.get(PreferenceKeys.AccessToken, "")
        if (accessToken.isNotEmpty()) {
            val jwt = JWT(accessToken)
            if (jwt.isExpired()) {
                emit(Result0.Loading)
                val oldToken = prefDataSource.get(PreferenceKeys.AccessToken, "")
                val refresh = prefDataSource.get(PreferenceKeys.RefreshToken, "")
                val newToken = api.refresh(oldToken, refresh)
                newToken.onSuccess { token->
                    val newAccessToken = token.replace("\"", "")
                    prefDataSource.set(PreferenceKeys.AccessToken, newAccessToken)
                    JWT(newAccessToken).getSessionId()?.let { sessionId->
                        prefDataSource.set(PreferenceKeys.SessionId, sessionId)
                    }
                }
                emit(newToken)
            }
        }
    }

    override fun getJWT(): JWT? {
        return try {
            prefDataSource.get(PreferenceKeys.AccessToken, "").getJWT()
        } catch (exception: Throwable) {
            Log.e("error to convert JWT", exception.localizedMessage.orEmpty())
            null
        }
    }

    override fun logOut() {
        prefDataSource.set(PreferenceKeys.AccessToken, "")
        prefDataSource.set(PreferenceKeys.SessionId, PreferenceDefaults.SessionId)
        prefDataSource.set(PreferenceKeys.RefreshToken, PreferenceDefaults.RefreshToken)
        prefDataSource.clearObject<List<Application>>(PreferenceKeys.Applications)
        prefDataSource.clearObject<List<Classmate>>(PreferenceKeys.Classmates)
        prefDataSource.clearObject<List<Deadline>>(PreferenceKeys.Deadlines)
        prefDataSource.clearObject<Info>()
        prefDataSource.clearObject<Marks>()
        prefDataSource.clearObject<Payments>()
        prefDataSource.clearObject<List<DialogModel>>(PreferenceKeys.Dialogs)
        prefDataSource.clearObject<Statements>()
    }

}