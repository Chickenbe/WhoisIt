package com.example.anton.idapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import com.google.android.gms.vision.face.Face
import kotlin.math.roundToInt

class ImageProperty(
    private val primaryImageWidth: Int,
    private val primaryImageHeight: Int,
    private val secondaryImageWidth: Int,
    private val secondaryImageHeight: Int
) {

    companion object {
        fun convertDpToPx(context: Context, dp: Float): Float = dp * context.resources.displayMetrics.density


        fun getIncreasedThumbnail(bitmap: Bitmap?, face: Face?) : Bitmap {
            val xIncreaseFactor = 0.35
            val yIncreaseFactor = 0.53
            val faceCenter = PointF(
                (face!!.position.x + face.width / 2.0F),
                (face.position.y + face.height / 2.0F))

            var newX = face.position.x - (face.width * xIncreaseFactor)
            newX = if (newX < 0) 0.0 else newX
            var newY = face.position.y - (face.height * yIncreaseFactor)
            newY = if (newY < 0) 0.0 else newY

            val distanceToCenterByX = faceCenter.x - newX
            val distanceToCenterByY = faceCenter.y - newY

            var newWidth = 2 * distanceToCenterByX
            newWidth = if (newX + newWidth > bitmap!!.width)
                (bitmap.width - newX) else newWidth
            var newHeight = 2 * distanceToCenterByY
            newHeight = if (newY + newHeight > bitmap.height)
                (bitmap.height - newY) else newHeight

            /*
                 var newWidth = face.width + 2 * (face.width * xIncreaseFactor)
                 Log.d("coordinate", "Width #1: $newWidth")
                 newWidth = if (newX + newWidth > mBitmap.width)
                     (mBitmap.width - newX) else newWidth
                 Log.d("coordinate", "Width #2: $newWidth")
                 var newHeight = face.height + 2 * (face.height * yIncreaseFactor)
                 Log.d("coordinate", "Height #1: $newHeight")
                 newHeight = if (newY + newHeight > mBitmap.height)
                     (mBitmap.height - newY) else newHeight
                 Log.d("coordinate", "Height #2: $newHeight")
             */

            return Bitmap.createBitmap(
                bitmap, newX.roundToInt(), newY.roundToInt(),
                newWidth.roundToInt(), newHeight.roundToInt())
        }

    }

    fun normalizeX(x: Float) : Float = x * (secondaryImageWidth.toFloat() / primaryImageWidth.toFloat())
    fun normalizeY(y: Float) : Float = y * (secondaryImageHeight.toFloat() / primaryImageHeight.toFloat())
}