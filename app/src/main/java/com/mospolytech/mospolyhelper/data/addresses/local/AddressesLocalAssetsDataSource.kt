package com.mospolytech.mospolyhelper.data.addresses.local

import android.util.Log
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import com.mospolytech.mospolyhelper.utils.TAG
import java.lang.Exception

class AddressesLocalAssetsDataSource {

    fun get(): Addresses? {
        return try {
            Klaxon().parse<Addresses>(App.getAsset("addresses.json")!!)
        } catch (e: Exception) {
            Log.d(TAG, "Addresses reading from assets and parsing exception", e)
            null
        }
    }
}