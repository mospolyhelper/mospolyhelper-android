package com.mospolytech.mospolyhelper.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt


class RoundedBackgroundSpan(
    private val backgroundColor: Int
) : ReplacementSpan() {

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val totalHeight = bottom - top

        val textRect = Rect()
        val textWidth = paint.measureText(text.toString(), start, end)
        var textHeight = textRect.height().toFloat()
        textHeight -= paint.ascent()
        //textHeight += paint.descent()

        var delta =  totalHeight - (bottom.toFloat() - (y - textHeight))
        delta /= 2.4f
        val y = y - delta
        val bottom = bottom - delta

        val rect = RectF(
            x,
            y - textHeight,
            x + textWidth,
            bottom.toFloat()
        )
        paint.color = backgroundColor
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint)
        paint.color = 0xffffffff.toInt()
        canvas.drawText(text, start, end, x.toFloat(), y.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).roundToInt()
    }


    companion object {
        private const val CORNER_RADIUS = 20f
    }


}