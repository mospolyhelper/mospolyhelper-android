package com.mospolytech.mospolyhelper.utils

import com.auth0.android.jwt.JWT

fun JWT.getSessionId() = this.getClaim("sessionId").asString()

fun JWT.getPermissions(): List<String> = this.getClaim("permissions").asList(String::class.java) ?: emptyList()

fun JWT.getName(): String? =
    this.getClaim("name").asString()

fun JWT.getAvatar(): String? = this.getClaim("avatarUrl").asString()

fun JWT.isExpired(): Boolean = this.isExpired(10)