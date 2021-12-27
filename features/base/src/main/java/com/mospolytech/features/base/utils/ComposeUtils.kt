package com.mospolytech.features.base.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit

@Composable
fun getContext() = LocalContext.current

@Composable
fun TextUnit.dp() =
    with(LocalDensity.current) {
        this@dp.toDp()
    }