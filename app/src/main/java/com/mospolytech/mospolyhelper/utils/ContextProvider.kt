package com.mospolytech.mospolyhelper.utils

import android.content.Context

class ContextProvider {
    companion object {
        var context: Context? = null
        private const val DATA_FOLDER = "appdata"


        fun getFilesDir() = context!!.filesDir.resolve(DATA_FOLDER)
    }
}