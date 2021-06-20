package com.mospolytech.mospolyhelper.features.utils

import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager(private val context: Context, layoutDirection: Int):
    LinearLayoutManager(context, layoutDirection, false) {

    companion object {
        // This determines how smooth the scrolling will be
        private
        const val MILLISECONDS_PER_INCH = 300f
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {

        val smoothScroller: LinearSmoothScroller = object: LinearSmoothScroller(context) {

            fun dp2px(dpValue: Float): Int {
                val scale = context.resources.displayMetrics.density
                return (dpValue * scale + 0.5f).toInt()
            }

            // change this and the return super type to "calculateDyToMakeVisible" if the layout direction is set to VERTICAL
            override fun calculateDxToMakeVisible(view: View?, snapPreference : Int): Int {
                return super.calculateDxToMakeVisible(view, SNAP_TO_END) - dp2px(50f)
            }

            //This controls the direction in which smoothScroll looks for your view
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@CustomLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            //This returns the milliseconds it takes to scroll one pixel.
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}