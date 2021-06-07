package com.mospolytech.mospolyhelper.data.utilities.addresses.local

import android.content.Context
import android.util.Log
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.TAG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AddressesLocalStorageDataSource(
    val context: Context
    ) {
    companion object {
        const val ADDRESSES_FOLDER = "addresses"
        const val ADDRESSES_FILE = "cached_addresses"
    }

    fun get(): AddressMap? {
        val file = context.filesDir.resolve(ADDRESSES_FOLDER).resolve(ADDRESSES_FILE)  // TODO: Add directory
        if (!file.exists()) {
            return null
        }
        return try {
            Json.decodeFromString<AddressMap>(file.readText())
        } catch (e: Exception) {
            Log.e(TAG, "Addresses reading from the local storage and parsing exception", e)
            null
        }
    }



    fun set(addressMap: AddressMap) {
        val file = context.filesDir.resolve(ADDRESSES_FOLDER).resolve(ADDRESSES_FILE)

        if (file.exists()) {
            file.deleteRecursively()
        } else {
            file.parentFile?.mkdirs()
        }

        try {
            file.createNewFile()
            file.writeText(Json.encodeToString(addressMap))
        } catch (e: Exception) {
            Log.e(TAG, "Addresses parsing and writing exception", e)
        }
    }
}