package com.mospolytech.mospolyhelper.utils

inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName