package com.mospolytech.mospolyhelper.data.addresses.remote

import android.util.Log
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import com.mospolytech.mospolyhelper.utils.TAG
import java.lang.Exception
import java.net.URL

class AddressesRemoteDataSource {
    companion object {
        const val ADDRESSES_URL =
            "https://raw.githubusercontent.com/mospolyhelper/up-to-date-information/master/addresses.json"
    }

    fun get(): Addresses? {
        return try {
            Klaxon().parse<Addresses>(URL(ADDRESSES_URL).readText())
        } catch(e: Exception) {
            Log.e(TAG, "Addresses downloading and parsing exception", e)
            null
        }
    }
}