package com.mospolytech.mospolyhelper.repository.dao

import android.util.Log
import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.TAG
import com.mospolytech.mospolyhelper.repository.models.Addresses
import com.mospolytech.mospolyhelper.utils.AssetProvider
import com.mospolytech.mospolyhelper.utils.ContextProvider
import java.io.File
import java.lang.Exception
import java.net.URL

class AddressesDao {
    companion object {
        const val AddressesFolder = "addresses"
        const val AddressesFile = "cached_addresses"
        const val AddressesUrl =
            "https://gist.githubusercontent.com/tipapro/f19b581ea759cacde6ff674b516c552a/raw/1920290b693458a68c57f1ecf853fea90544d2a9/mospolyhelper-addresses.json"
    }

    fun readAddresses(): Addresses? {
        val filePath = ContextProvider.getFilesDir().resolve(AddressesFolder).resolve(AddressesFile)  // TODO: Add directory
        return if (!filePath.exists()) {
            null
        } else {
            val serBuildings = filePath.readText()
            Klaxon().parse<Addresses>(serBuildings)
        }
    }

    fun downloadAddresses(): Addresses? {
        return try {
            val serBuildings = URL(AddressesUrl).readText()
            Klaxon().parse<Addresses>(serBuildings)
        } catch(e: Exception) {
            Log.e(TAG, "Addresses downloading and parsing error", e)
            null
        }
    }

    fun saveAddresses(buildings: Addresses) {
        val filePath = File(ContextProvider.getFilesDir().resolve(AddressesFolder).resolve(AddressesFile), AddressesFile)
        filePath.delete()
        val str = Klaxon().toJsonString(buildings)
        filePath.parentFile?.mkdirs()
        filePath.createNewFile()
        filePath.writeText(str)
    }

    fun getAddressesFromAssets(): Addresses? {
        return Klaxon().parse<Addresses>(AssetProvider.getAsset("addresses.json")!!)
    }

    fun getAddresses(downloadNew: Boolean): Addresses? {
        var addresses: Addresses? = null
        if (!downloadNew) {
            try {
                addresses = readAddresses()
            } catch (ex: Exception) {

            }
        }
        if (addresses == null) {
            addresses = downloadAddresses()
            if (addresses == null) {
                if (downloadNew) {
                    try {
                        addresses = readAddresses()
                        if (addresses != null) {
                            return addresses
                        }
                    } catch (ex: Exception) {

                    }
                }
                addresses = getAddressesFromAssets()
            } else {
                saveAddresses(addresses)
            }
        }
        return addresses
    }
}