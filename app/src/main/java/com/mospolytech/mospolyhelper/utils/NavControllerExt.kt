package com.mospolytech.mospolyhelper.utils

import android.util.Log
import androidx.navigation.NavController

fun NavController.safe(action: NavController.() -> Unit) {
    try {
        action.invoke(this)
    } catch (e: Exception) {
        Log.e(TAG, "NavController exception: ", e)
    }
}