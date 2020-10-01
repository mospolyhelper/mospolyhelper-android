package com.mospolytech.mospolyhelper.features.custom_views


import android.graphics.*
import android.graphics.Paint.Style.*
import android.graphics.drawable.Drawable
import com.mospolytech.mospolyhelper.features.custom_views.SuperellipseLogic.getCachedBitmap
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class SuperEllipse(
    color: Int,
    strokeWidth: Float = 0f,
    paintStyle: Paint.Style = Paint.Style.FILL,
    strokeColor: Int = 0
): Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var strokeColor: Int = -1

    private var width = 0

    private var height = 0

    private var bitmapPadding = 0

    private var squircleBitmap: Bitmap? = null

    init {
        paint.apply {
            this.color = color
            this.strokeWidth = strokeWidth
            style = paintStyle
        }
        this.strokeColor = strokeColor
        bitmapPadding = paint.strokeWidth.toInt()
    }

    override fun draw(canvas: Canvas) {
        if (squircleBitmap == null) {
            return
        }
        /**
         * Draw a previously instantiated Bitmap instead of a path, this will increase performance drastically.
         * The bitmap is already centered for you.
         */
        canvas.drawBitmap(squircleBitmap!!, 0f, 0f, null)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        width = bounds.width()
        height = bounds.height()
        /**
         * Request a new squircle bitmap, this is a smart method and it will only return a new Bitmap
         * instance if it cannot find a previously created Bitmap with the same size (width and height),
         * this will increase performance, specially for things like recycler views or list views.
         */

        squircleBitmap = getCachedBitmap(width, height, bitmapPadding, paint, strokeColor)
    }
}

object SuperellipseLogic {

    private const val DEF_CORNERS_CONSTANT = 0.3

    private val cachedBitmaps = Hashtable<String, Bitmap>()

    private val canvas = Canvas()

    private var path = Path()

    /**
     * Notice that a cached bitmap will be returned if the width,
     * height and color of the arguments matches any of the values
     * of any other cached bitmap.
     * These values serve as a key for the hashtable.
     *
     * @return if(cachedBitmap) cachedBitmap else freshBitmap.
     */
    fun getCachedBitmap(w: Int, h: Int, padding: Int, paint: Paint, sC: Int): Bitmap {
        val k = "$w$h${paint.color}"
        if (cachedBitmaps[k] == null)
            cachedBitmaps[k] = getSquircleBitmapBackground(w, h, padding, paint, sC)
        return cachedBitmaps[k]!!
    }

    /**
     * Use this method if you don't want to cache bitmaps or receive previously cached bitmaps.
     * @return freshBitmap.
     */
    fun getBitmap(w: Int, h: Int, padding: Int, paint: Paint, sC: Int) =
        getSquircleBitmapBackground(w, h, padding, paint, sC)

    /**
     * @param w width of the bitmap.
     * @param h height of the bitmap.
     * @param strokeColor color of the stroke (only applies if Paint.style == STROKE || STROKE_AND_FILL)
     * @param padding bitmap padding (this will not off-center the bitmap).
     * @param paint paint to use for drawing the bitmap.
     * If canvas not centered, center it.
     * Create drawing bitmap.
     * Draw squircle path on bitmap.
     * @return squircleBitmap.
     */
    private fun getSquircleBitmapBackground(
        w: Int,
        h: Int,
        padding: Int,
        paint: Paint,
        strokeColor: Int
    ): Bitmap {
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        this.canvas.setBitmap(b)
        this.canvas.translate(w / 2f, h / 2f)

        recalculatePath((w / 2) - padding, (h / 2) - padding)


        val ogStl = paint.style
        val ogClr = paint.color

        if (ogStl == FILL || ogStl == FILL_AND_STROKE) {
            canvas.drawPath(path, paint)
        }
        if (ogStl == STROKE || ogStl == FILL_AND_STROKE) {

            paint.color = strokeColor
            paint.style = STROKE

            canvas.drawPath(path, paint)
            //Reassign values to not interfere with objects og values
            paint.style = ogStl
            paint.color = ogClr
        }

        return b
    }


    fun getSuperEllipsePath(
        radX: Int,
        radY: Int,
        corners: Double = DEF_CORNERS_CONSTANT
    ): Path {
        val newPath = Path()
        addSuperEllipseToPath(newPath, radX, radY, corners)
        return newPath
    }

    private fun addSuperEllipseToPath(p: Path, radX: Int, radY: Int, corners: Double) {
        val cornerX = corners / (radX.toFloat() / radY)
        var l = 0.0
        var angle: Double
        for (i in 0 until 360) {
            angle = Math.toRadians(l)
            val x = getX(radX, angle, cornerX)
            val y = getY(radY, angle, corners)
            if (i == 0) {
                p.moveTo(x, y)
            }
            l++
            angle = Math.toRadians(l)
            val x2 = getX(radX, angle, cornerX)
            val y2 = getY(radY, angle, corners)
            p.lineTo(x2, y2)
        }
        p.close()
    }


    private fun recalculatePath(radX: Int, radY: Int, corners: Double = DEF_CORNERS_CONSTANT) {
        path.reset()
        addSuperEllipseToPath(path, radX, radY, corners)
        path.close()
    }

    // x = cos(angle)
    private fun getX(radX: Int, angle: Double, corners: Double) =
        (abs(cos(angle)).pow(corners) * radX * sgn(
            cos(angle)
        )).toFloat()

    // y = sin(angle)
    private fun getY(radY: Int, angle: Double, corners: Double) =
        (abs(sin(angle)).pow(corners) * radY * sgn(
            sin(angle)
        )).toFloat()

    private fun sgn(value: Double) = if (value > 0.0) 1.0 else if (value < 0.0) -1.0 else 0.0

    /**
     * I suggest you call this method on the activity's on destroy event
     */
    fun release() {
        cachedBitmaps.forEach {
            it.value.recycle()
        }
        cachedBitmaps.clear()
    }

}
