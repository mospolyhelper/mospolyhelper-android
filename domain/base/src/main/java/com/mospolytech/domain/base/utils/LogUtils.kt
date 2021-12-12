package com.mospolytech.domain.base.utils

import kotlin.reflect.KClass

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

val <T : Any> KClass<T>.TAG: String
    get() = java.simpleName