package com.mospolytech.mospolyhelper.data.utilities.addresses.remote

import android.util.Log
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class AddressesRemoteDataSource {
    companion object {
        const val ADDRESSES_URL =
            "https://raw.githubusercontent.com/mospolyhelper/up-to-date-information/master/addresses.json"
    }

    fun get(): AddressMap? {
        return try {
            Json.decodeFromString<AddressMap>(URL(ADDRESSES_URL).readText())
        } catch(e: Exception) {
            Log.e(TAG, "Addresses downloading and parsing exception", e)
            null
        }
    }
}