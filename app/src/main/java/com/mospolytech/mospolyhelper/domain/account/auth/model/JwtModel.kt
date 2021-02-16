package com.mospolytech.mospolyhelper.domain.account.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class JwtModel(val accessToken: String, val refreshToken: String)