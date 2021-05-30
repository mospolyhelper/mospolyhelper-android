package com.mospolytech.mospolyhelper.data.account.auth.local

import android.util.Log
import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.utils.PreferenceKeys

class AuthJwtLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun set(accessToken: String) {
        prefDataSource.set(PreferenceKeys.AccessToken, accessToken)
    }

    fun get(): JWT? {
        return try {
            val jwt = prefDataSource.get(PreferenceKeys.AccessToken, "")
            if (jwt.isNotEmpty())
                JWT(jwt)
            else
                null
        } catch (e: Exception) {
            Log.e("JWT", e.toString())
            null
        }
    }

    fun clear() {
        prefDataSource.set(PreferenceKeys.AccessToken, "")
    }

}