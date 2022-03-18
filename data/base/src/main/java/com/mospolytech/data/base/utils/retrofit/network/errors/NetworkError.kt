package com.mospolytech.data.base.utils.retrofit.network.errors

import java.io.IOException

/**
 * Network error
 */
data class NetworkError(val error: IOException) : Exception()