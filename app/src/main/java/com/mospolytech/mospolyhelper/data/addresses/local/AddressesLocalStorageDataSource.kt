package com.mospolytech.mospolyhelper.data.addresses.local

import android.util.Log
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.TAG

class AddressesLocalStorageDataSource {
    companion object {
        const val ADDRESSES_FOLDER = "addresses"
        const val ADDRESSES_FILE = "cached_addresses"
    }

    fun get(): AddressMap? {
        val file = App.context!!.filesDir.resolve(ADDRESSES_FOLDER).resolve(ADDRESSES_FILE)  // TODO: Add directory
        if (!file.exists()) {
            return null
        }
        return try {
            Klaxon().parse<AddressMap>(file.readText())
        } catch (e: Exception) {
            Log.e(TAG, "Addresses reading from the local storage and parsing exception", e)
            null
        }
    }



    fun set(addressMap: AddressMap) {
        val file = App.context!!.filesDir.resolve(ADDRESSES_FOLDER).resolve(ADDRESSES_FILE)

        if (file.exists()) {
            file.deleteRecursively()
        } else {
            file.parentFile?.mkdirs()
        }

        try {
            file.createNewFile()
            file.writeText(Klaxon().toJsonString(addressMap))
        } catch (e: Exception) {
            Log.e(TAG, "Addresses parsing and writing exception", e)
        }
    }
}