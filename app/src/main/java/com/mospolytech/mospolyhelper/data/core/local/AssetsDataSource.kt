package com.mospolytech.mospolyhelper.data.core.local

import android.content.res.AssetManager
import java.io.IOException

class AssetsDataSource(
    private val assetManager: AssetManager
) {
    fun get(assetName: String) =
        getAsset(assetName)?.bufferedReader()?.use { it.readText() }

    private fun getAsset(assetName: String) = try {
        assetManager.open(assetName)
    } catch (e: IOException ) {
        null
    }
}