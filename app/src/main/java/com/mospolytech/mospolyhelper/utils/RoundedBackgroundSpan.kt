package com.mospolytech.mospolyhelper.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt


class RoundedBackgroundSpan(
    private val backgroundColor: Int,
    private val textColor: Int? = null,
    private val text: String,
    private val relativeTextSize: Float = 0.65f,
    private val cornerRadiusPercentage: Float = 0.26f // 0.275f
) : ReplacementSpan(), LineHeightSpan {

    companion object {
        private const val horizontalGrowthRate = 1.65f
        private const val additionTextRatio = 0.85f  // To add some space in bottom of feature rect
    }

    private var height: Int = 0

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

        val descent = paint.descent()
        val ascent = paint.ascent()
        val ascentAbs = baseline + ascent
        val paint = Paint(paint)


        val totalHeight = descent - ascent

        // For line space
        val totalHeight0 = -ascent
        val ratio = totalHeight0 / totalHeight
        val baselineDelta0 = descent * ratio

        val newBaseline0 = baseline - baselineDelta0


        // For text ratio
        val heightDelta = totalHeight0 - totalHeight0 * relativeTextSize
        val baselineDelta = (baseline - newBaseline0) * relativeTextSize

        val newTopOfLine = ascentAbs + heightDelta / 2f
        val newBottomOfLine = baseline - heightDelta / 2f
        val newBaseLine = newBottomOfLine - baselineDelta


        // For addition text ratio
        val height1 = newBottomOfLine - newTopOfLine

        val pseudoNewBottomOfLine = baseline - height1 * additionTextRatio
        val baselineDelta1 = (pseudoNewBottomOfLine - newBaseLine) * additionTextRatio
        val newBaseLine1 = pseudoNewBottomOfLine - baselineDelta1

        paint.textSize *= relativeTextSize * ratio * additionTextRatio


        var textWidth = paint.measureText(this.text.toString(), start, end)

        val horizontalPadding = paint.descent() * 2 * horizontalGrowthRate


        val bias = (bottomOfLine - baseline) / 2f

        var width = textWidth + horizontalPadding * 2f
        if (width > canvas.width - x) {
            width = canvas.width - x
            textWidth = width - horizontalPadding * 2f
        }

        val rect = RectF(
            x,
            topOfLine.toFloat() + bias,
            x + width,
            baseline.toFloat() + bias
        )

        val cornerRadius = rect.height() * cornerRadiusPercentage

        paint.color = backgroundColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        paint.color = textColor ?: 0xffffffff.toInt()



        val ellipsised = TextUtils.ellipsize(
            this.text.substring(start, end),
            TextPaint(paint),
            textWidth,
            TextUtils.TruncateAt.END
        ).toString()



        canvas.drawText(
            ellipsised,
            0,
            ellipsised.length,
            x + horizontalPadding,
            newBaseLine1.toFloat() + bias,
            paint
        )
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        //val paint = Paint(paint)
        fm?.let {
            height = it.descent - it.ascent
        }
        val descent = paint.descent()
        val ascent = paint.ascent()
        val paint = Paint(paint)


        val totalHeight = descent - ascent
        val totalHeight0 = -ascent
        val ratio = totalHeight0 / totalHeight

        paint.textSize *= relativeTextSize * ratio * additionTextRatio

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
        fm?.let {
            height = it.descent - it.ascent
        }

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