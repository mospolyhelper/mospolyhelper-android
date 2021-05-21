package com.mospolytech.mospolyhelper.data.utilities.addresses.local

import android.util.Log
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AddressesLocalAssetsDataSource {

    fun get(): AddressMap? {
        return try {
            Json.decodeFromString<AddressMap>(App.getAsset("addresses.json")!!.bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            Log.d(TAG, "Addresses reading from assets and parsing exception", e)
            null
        }
    }
}