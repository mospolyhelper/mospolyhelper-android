package com.mospolytech.data.base.model

data class Cached<T>(
    val data: Result<T>,
    val isExpired: Boolean
)