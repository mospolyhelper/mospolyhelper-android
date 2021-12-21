package com.mospolytech.data.account.model

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(val result: T)

inline fun<reified T> T.toResponse(): Response<T> = Response(this)
