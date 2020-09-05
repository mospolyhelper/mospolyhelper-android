package com.mospolytech.mospolyhelper.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt


class RoundedBackgroundSpan(
    private val backgroundColor: Int,
    private val textColor: Int? = null,
    private val height: Int,
    private val text: String
) : ReplacementSpan(), LineHeightSpan {

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
        val start = 0
        val end = this.text.length
        val paint = Paint(paint)
        paint.textSize = height * 0.7f

        val totalHeight = bottom - top
        val textRect = Rect()
        val textWidth = paint.measureText(this.text.toString(), start, end)
        var textHeight = textRect.height().toFloat()
        textHeight -= paint.ascent()
        //textHeight += paint.descent()


        val horizontalPadding = paint.descent() * 2

        var delta =  totalHeight - (bottom.toFloat() - (y - textHeight))
        delta /= 3f

        val y = y - delta
        val bottom = bottom - delta

        val topGrowthRate = 0.08f
        val bottomGrowthRate = 0.12f

        val topAdd = totalHeight * topGrowthRate / 2f
        val bottomAdd = totalHeight * bottomGrowthRate / 2f

        val rect = RectF(
            x,
            y - textHeight - topAdd,
            x + textWidth + horizontalPadding * 2,
            bottom + bottomAdd
        )

        val cornerRadius = rect.height() * 0.2f

        paint.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = textColor ?: 0xffffffff.toInt()
        canvas.drawText(this.text, start, end, x.toFloat() + horizontalPadding, y.toFloat(), paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val paint = Paint(paint)
        paint.textSize = height * 0.7f
        val start = 0
        val end = this.text.length
        return (paint.measureText(this.text, start, end) + paint.descent() * 4).roundToInt()
    }

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
//        if (fm == null) return
//        val paint = Paint()
//        paint.textSize = height.toFloat()
//        val newFm = paint.fontMetricsInt
//        fm.descent = newFm.descent
//        fm.bottom = newFm.bottom
//        fm.ascent = newFm.ascent
//        fm.leading = -10//newFm.leading
//        fm.top = newFm.top


//        val originHeight = fm.descent - fm.ascent
//        // If original height is not positive, do nothing.
//        if (originHeight <= 0) {
//            return
//        }
//        val ratio = height * 1.0f / originHeight
//        fm.descent = (fm.descent * ratio).roundToInt()
//        fm.ascent = fm.descent - height
    }
}