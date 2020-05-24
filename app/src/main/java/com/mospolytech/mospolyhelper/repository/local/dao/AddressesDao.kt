package com.mospolytech.mospolyhelper.repository.local.dao

import com.beust.klaxon.Klaxon
import com.mospolytech.mospolyhelper.repository.models.Addresses
import com.mospolytech.mospolyhelper.utils.AssetProvider
import java.io.File
import java.lang.Exception
import java.net.URL
import java.nio.file.Paths

class AddressesDao {
    companion object {
        const val BuildingsFile = "cached_addresses"
        const val BuildingsUrl =
            "https://gist.githubusercontent.com/tipapro/" +
                    "f19b581ea759cacde6ff674b516c552a/raw/91385341b7e36bd8fb48a8d4d3abfffef5614e42/" +
                    "mospolyhelper-addresses.json"
    }

    fun readAddresses(): Addresses? {
        val filePath = File("", BuildingsFile)  // TODO: Add directory
        return if (!filePath.exists()) {
            null
        } else {
            val serBuildings = filePath.readText()
            Klaxon().parse<Addresses>(serBuildings)
        }
    }

    fun downloadAddresses(): Addresses? {
        return try {
            val serBuildings = URL(BuildingsUrl).readText()
            Klaxon().parse<Addresses>(serBuildings)
        } catch(e: Exception) {
            null
        }
    }

    fun saveAddresses(buildings: Addresses) {
        val filePath = File("", BuildingsFile)
        filePath.delete()
        val str = Klaxon().toJsonString(buildings)
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