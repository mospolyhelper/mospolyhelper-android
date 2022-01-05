package com.mospolytech.features.base.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerState.onPageChanged(action: suspend (page: Int) -> Unit) {
    LaunchedEffect(this) {
        snapshotFlow { currentPage }.collect { page ->
            action(page)
        }
    }
}