package com.mospolytech.data.base.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.asRequestImage(): RequestBody {
    return asRequestBody("image/*".toMediaTypeOrNull())
}