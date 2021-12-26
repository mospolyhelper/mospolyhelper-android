package com.mospolytech.features.base.utils

import java.util.Objects.isNull

fun Any?.isNull() = this == null

fun Any?.isNotNull() = !isNull()