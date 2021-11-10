package com.mospolytech.mospolyhelper.domain.account.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class JwtModel(val accessToken: String, val refreshToken: String)