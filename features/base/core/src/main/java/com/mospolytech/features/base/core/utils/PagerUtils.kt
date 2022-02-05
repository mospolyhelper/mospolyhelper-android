package com.mospolytech.features.base.core.utils

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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerState.bindTo(position: Int, animated: Boolean = false) {
    LaunchedEffect(position, animated) {
        if (currentPage != position) {
            if (animated) {
                animateScrollToPage(position)
            } else {
                scrollToPage(position)
            }
        }
    }
}