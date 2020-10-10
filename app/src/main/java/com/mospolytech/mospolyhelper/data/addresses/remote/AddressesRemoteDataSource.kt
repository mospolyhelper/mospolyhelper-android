package com.mospolytech.mospolyhelper.data.addresses.remote

import android.util.Log
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.TAG
import java.lang.Exception
import java.net.URL

class AddressesRemoteDataSource {
    companion object {
        const val ADDRESSES_URL =
            "https://raw.githubusercontent.com/mospolyhelper/up-to-date-information/master/addresses.json"
    }

    fun get(): AddressMap? {
        return try {
            Klaxon().parse<AddressMap>(URL(ADDRESSES_URL).readText())
        } catch(e: Exception) {
            Log.e(TAG, "Addresses downloading and parsing exception", e)
            null
        }
    }
}