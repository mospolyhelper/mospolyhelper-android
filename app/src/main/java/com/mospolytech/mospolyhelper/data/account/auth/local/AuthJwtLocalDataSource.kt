package com.mospolytech.mospolyhelper.data.account.auth.local

import android.util.Log
import com.auth0.android.jwt.JWT
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import com.mospolytech.mospolyhelper.domain.account.info.model.Info
import com.mospolytech.mospolyhelper.utils.PreferenceKeys
import com.mospolytech.mospolyhelper.utils.Result
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.lang.reflect.TypeVariable

class AuthJwtLocalDataSource(private val prefDataSource: SharedPreferencesDataSource) {

    fun set(accessToken: String) {
        prefDataSource.setString(PreferenceKeys.AccessToken, accessToken)
    }

    fun get(): JWT? {
        return try {
            val jwt = prefDataSource.getString(PreferenceKeys.AccessToken, "")
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
        prefDataSource.setString(PreferenceKeys.AccessToken, "")
    }

}