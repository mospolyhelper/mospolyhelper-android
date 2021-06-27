package com.mospolytech.mospolyhelper.data.account.auth.local

import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.utils.PreferenceKeys

class AuthLocalDataSource(
    private val prefDataSource: SharedPreferencesDataSource
) {

    var jwt: JWT?
    get() {
        val token = prefDataSource.get(PreferenceKeys.AccessToken, "")
        return if (token.isNotEmpty())
            JWT(token)
        else
            null
    }
    private set(value) { }

    var accessToken: String? = null
    get() {
        val token = prefDataSource.get(PreferenceKeys.AccessToken, "")
        return if (token.isNotEmpty())
            token
        else
            null
    }
    set(value) {
        value?.let {
            prefDataSource.set(PreferenceKeys.AccessToken, it)
        }
        field = value
    }

    fun clearToken() {
        prefDataSource.set(PreferenceKeys.AccessToken, "")
    }

}