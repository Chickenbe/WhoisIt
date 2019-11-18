package com.example.anton.idapplication.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.example.anton.idapplication.R
import com.example.anton.idapplication.utils.ImageProperty
import kotlinx.android.synthetic.main.activity_photo_view.*
import kotlin.math.roundToInt


class PhotoActivity : AppCompatActivity() {

    private val widthIncreaseFactor = 0.35
    private val heightIncreaseFactor = 0.4
    private val xIncreaseFactor = 0.35
    private val yIncreaseFactor = 0.53

    lateinit var imageProp: ImageProperty
    lateinit var mBitmap: Bitmap
    private var isFaceDetected = false
    var bitmapCopy: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        setSupportActionBar(findViewById(R.id.photoViewToolBar))

        setUpBitmap()
        photoView.setImageBitmap(mBitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBitmap.recycle()
    }

    private fun setUpBitmap() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, R.drawable.face_image, options)
        options.inSampleSize = calculateInSampleSize(options, 720, 1080)
        options.inJustDecodeBounds = false
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.face_image, options)
    }

    private fun detectFace(bitmap: Bitmap) : Face? {
        val faceDetector = FaceDetector.Builder(applicationContext)
            .setTrackingEnabled(false)
            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
            .build()
        if (!faceDetector.isOperational) {
            Toast.makeText(applicationContext, "Not operational", Toast.LENGTH_SHORT).show()
            finish()
        }
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val faces = faceDetector.detect(frame)
        if (faces.size() == 0) {
            Toast.makeText(applicationContext, "Face not found", Toast.LENGTH_SHORT).show()
            return null
        }
        return faces.valueAt(0)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_view_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.detect_face_action -> {
                val face = detectFace(mBitmap)
                if (face != null) {
                    bitmapCopy = getIncreasedThumbnail(face)
                    isFaceDetected = true
                    photoView.setImageBitmap(bitmapCopy)
                    invalidateOptionsMenu()
                }
            }
            R.id.zoom_action -> {
                photoView.visibility = View.GONE
                circularImageView.visibility = View.VISIBLE
                circularImageView.setImageBitmap(bitmapCopy)
            }
            R.id.back_action -> {
                if (bitmapCopy != null) {
                    circularImageView.visibility = View.GONE
                    photoView.visibility = View.VISIBLE
                    photoView.setImageBitmap(bitmapCopy)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val detectFaceItem = menu?.findItem(R.id.detect_face_action)
        val zoomPhotoItem = menu?.findItem(R.id.zoom_action)
        val backToPhotoItem = menu?.findItem(R.id.back_action)
        if (isFaceDetected) {
            detectFaceItem?.isVisible = false
            zoomPhotoItem?.isVisible = true
            backToPhotoItem?.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }


    private fun getIncreasedThumbnail(face: Face?) : Bitmap {
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
        newWidth = if (newX + newWidth > mBitmap.width)
            (mBitmap.width - newX) else newWidth
        var newHeight = 2 * distanceToCenterByY
        newHeight = if (newY + newHeight > mBitmap.height)
            (mBitmap.height - newY) else newHeight

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
            mBitmap, newX.roundToInt(), newY.roundToInt(),
            newWidth.roundToInt(), newHeight.roundToInt())
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image

        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width

            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

}