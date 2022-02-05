package com.mospolytech.features.base.elements

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpacerWidth(width: Dp, modifier: Modifier = Modifier) {
    Spacer(modifier.width(width))
}

@Composable
fun SpacerWidth(intrinsicSize: IntrinsicSize, modifier: Modifier = Modifier) {
    Spacer(modifier.width(intrinsicSize))
}

@Composable
fun SpacerHeight(height: Dp, modifier: Modifier = Modifier) {
    Spacer(modifier.height(height))
}

@Composable
fun SpacerHeight(intrinsicSize: IntrinsicSize, modifier: Modifier = Modifier) {
    Spacer(modifier.height(intrinsicSize))
}

@Composable
fun SpacerSize(size: Dp, modifier: Modifier = Modifier) {
    Spacer(modifier.size(size))
}