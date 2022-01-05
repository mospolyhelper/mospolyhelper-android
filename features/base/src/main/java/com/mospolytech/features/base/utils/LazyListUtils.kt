package com.mospolytech.features.base.utils

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.isItemFullyVisible(index: Int): Boolean {
    return layoutInfo.visibleItemsInfo.any {
            val startItemPoint = it.offset
            val endItemPoint = it.offset + it.size

            it.index == index
                    && endItemPoint <= layoutInfo.viewportEndOffset
                    && startItemPoint >= 0
        }
}