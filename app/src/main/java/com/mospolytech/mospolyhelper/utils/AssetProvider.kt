package com.mospolytech.mospolyhelper.utils

import android.content.res.AssetManager
import java.io.InputStream

class AssetProvider {
    companion object {
        lateinit var assetManager: AssetManager

        fun getAsset(assetName: String): InputStream? {
            return try {
                assetManager.open(assetName)
            } catch (ex: Exception ) {
                null
            }
        }

    }
}