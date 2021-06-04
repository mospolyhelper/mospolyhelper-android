package com.mospolytech.mospolyhelper.features.utils

import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * Set the currently selected page. Smooth animation will be performed
 * based on [smoothMaxRange] value. Silently ignored if the adapter is not set
 * or empty. Clamps item to the bounds of the adapter.
 *
 * @param item Item index to select
 * @param smoothMaxRange Max range between current item and item to select,
 * above which there won't be used smooth scrolling
 */
fun ViewPager2.setSmartCurrentItem(item: Int, smoothMaxRange: Int = 3) {
    if (abs(currentItem - item) > smoothMaxRange) {
        setCurrentItem(item, false)
    } else {
        setCurrentItem(item, true)
    }
}