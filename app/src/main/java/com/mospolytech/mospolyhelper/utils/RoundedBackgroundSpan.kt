package com.mospolytech.mospolyhelper.utils

import android.graphics.*
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import com.mospolytech.mospolyhelper.App
import com.mospolytech.mospolyhelper.R
import kotlin.math.roundToInt


class RoundedBackgroundSpan(
    private val backgroundColor: Int,
    private val textColor: Int? = null,
    private val height: Int,
    private val text: String
) : ReplacementSpan(), LineHeightSpan {

    companion object {
        private const val relativeTextSize = 0.7f
        private const val topGrowthRate = 0.21f//0.27f
        private const val bottomGrowthRate = 0.14f
        private const val horizontalGrowthRate = 1.4f
        private const val cornerRadiusPercentage = 0.275f // 0.275f
        private const val bottomSpacing = 1f
    }

    private fun convertColorToNight(color: Int): Int {
        val hsv = FloatArray(3)
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv)
        hsv[2] = hsv[2] * 0.4f
        hsv[1] = 1f
        return Color.HSVToColor(hsv)
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        topOfLine: Int,
        baseline: Int,
        bottomOfLine: Int,
        paint: Paint
    ) {
        val start = 0
        val end = this.text.length

        val paint = Paint(paint)
        paint.textSize = height * relativeTextSize
        val totalHeight = bottomOfLine - topOfLine

        val textHeight = -paint.ascent()
        //textHeight += paint.descent()

        val textWidth = paint.measureText(this.text.toString(), start, end)

        val horizontalPadding = paint.descent() * 2 * horizontalGrowthRate

        var delta =  baseline - textHeight - topOfLine
        delta /= 3f

        val baseline = baseline - delta
        val bottomOfLine = bottomOfLine - delta

        val topAdd = totalHeight * topGrowthRate / 2f
        val bottomAdd = totalHeight * bottomGrowthRate / 2f

        val rect = RectF(
            x,
            baseline - textHeight - topAdd,
            x + textWidth + horizontalPadding * 2f,
            bottomOfLine + bottomAdd
        )

        val cornerRadius = rect.height() * cornerRadiusPercentage

        paint.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = textColor ?: 0xffffffff.toInt()

        canvas.drawText(this.text, start, end, x + horizontalPadding, baseline, paint)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val paint = Paint(paint)
        paint.textSize = height * relativeTextSize
        val start = 0
        val end = this.text.length
        val horizontalPadding = paint.descent() * 2 * horizontalGrowthRate
        return (paint.measureText(this.text, start, end) + horizontalPadding * 2).roundToInt()
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