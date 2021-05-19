package com.mospolytech.mospolyhelper.features.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * To solve viewpager - recyclerview conflict when you try to scroll vertically
 */
object RecyclerViewInViewPagerHelper : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN &&
            rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING
        ) {
            rv.stopScroll()
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit
}