package com.example.anton.idapplication.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.anton.idapplication.R
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.gms.vision.text.TextBlock
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.util.isNotEmpty
import androidx.viewpager.widget.ViewPager
import com.example.anton.idapplication.MyImageView
import com.google.android.gms.vision.text.Text
import com.example.anton.idapplication.adapter.MyPagerAdapter
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.utils.ImageProperty
import com.example.anton.idapplication.utils.ResultCode
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_add_new_data.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AddNewDataActivity : AppCompatActivity() {
    private var currentPageNumber = 0
    private var mBitMap: Bitmap? = null
    private var newImageProperty: ImageProperty? = null
    private var viewPager: ViewPager? = null
    private var pagerAdapter: MyPagerAdapter? = null
    private val propertyNameList = arrayListOf("Name", "Last name", "Middle name", "Birthday", "ID number")
    private val SECONDARY_PHOTO_WIDTH: Float = 345f
    private val SECONDARY_PHOTO_HEIGHT: Float = 454f
    private var textElementRectList: MutableList<Text> = mutableListOf()
    var mFace: Face? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_data)

        val imageUri = intent.getStringExtra("imageURI")
        val replyIntent = Intent()

        viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager?.addOnPageChangeListener(MyPageListener())
        pagerAdapter = MyPagerAdapter(supportFragmentManager, propertyNameList)
        viewPager?.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager, true)

        if (TextUtils.isEmpty(imageUri)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        } else {
            val uri: Uri = imageUri.toUri()
            mBitMap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            setNewImageDimensions()
            imageViewPrimary.setImageBitmap(mBitMap)
            imageViewPrimary.setBackgroundResource(R.drawable.image_border)
            recognizeText(imageViewPrimary)
            detectFace()
        }
    }



    private fun recognizeText(imageViewPrimary: ImageView) {
        var isOperationalFlag = true
        val handler = Handler()
        progressBar.visibility = ProgressBar.VISIBLE
        Thread(Runnable {
            val textRecognizer: TextRecognizer = TextRecognizer.Builder(applicationContext).build()
            if (!textRecognizer.isOperational) { isOperationalFlag = false }
            else {
                val frame: Frame = Frame.Builder().setBitmap(mBitMap).build()
                val items = textRecognizer.detect(frame)

                for (i in 0 until items.size()) {
                    val item = items.valueAt(i) as TextBlock
                    for(line in item.components) {
                        for (word in line.components) {
                            textElementRectList.add(word)
                        }
                    }
                }
            }

            handler.post {
                if (textElementRectList.isNotEmpty()) {
                    setUpNewImageView(imageViewPrimary)
                }
                else if (!isOperationalFlag) {
                    setResult(ResultCode.RESULT_TEXT_RECOGNITION_SUPPORT_ERROR)
                    finish()
                }
                else {
                    setResult(ResultCode.RESULT_RECOGNITION_ERROR)
                    finish()
                }
            }
        }).start()
    }

    private fun detectFace() {
        var isOperational = true
        var faceDetected = true
        val handler = Handler()
        Thread(Runnable {
            val faceDetector = FaceDetector.Builder(applicationContext)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build()
            if (!faceDetector.isOperational) { isOperational = false }
            else {
                val frame = Frame.Builder().setBitmap(mBitMap).build()
                val faces = faceDetector.detect(frame)
                if (faces.isNotEmpty()) {
                    mFace = faces.valueAt(0)
                } else {
                    faceDetected = false
                }
            }

            handler.post {
                if (!isOperational) {
                    setResult(ResultCode.RESULT_FACE_DETECTOR_SUPPORT_ERROR)
                    finish()
                }else if (!faceDetected) {
                    setResult(ResultCode.RESULT_FACE_DETECTION_ERROR)
                    finish()
                } else {
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
            }
        }).start()
    }


    private fun setUpNewImageView(imageViewPrimary: ImageView) {
        var imageViewSecondary: MyImageView? = null
        val mainLayOut = findViewById<FrameLayout>(R.id.frameLayoutID)
        val layOutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT )
        if (imageViewSecondary != null) { mainLayOut.removeView(imageViewSecondary) }

        mainLayOut.removeView(imageViewPrimary)
        imageViewSecondary = MyImageView(applicationContext, newImageProperty, textElementRectList).apply {
            layoutParams = layOutParams
            scaleType = ImageView.ScaleType.FIT_XY
            setImageBitmap(mBitMap)
            setPadding(0,0,0,0)
            setBackgroundResource(R.drawable.image_border)
            layOutParams.width = ImageProperty.convertDpToPx(applicationContext, SECONDARY_PHOTO_WIDTH).toInt()
            layOutParams.height = ImageProperty.convertDpToPx(applicationContext, SECONDARY_PHOTO_HEIGHT).toInt()
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    setUpTextByCoord(event.x, event.y)
                }
                    true }
        }
        mainLayOut.addView(imageViewSecondary)
    }


    private fun setNewImageDimensions() {
        newImageProperty = ImageProperty(
            //primary image
            mBitMap!!.width,
            mBitMap!!.height,
            //secondary image
            ImageProperty.convertDpToPx(applicationContext, SECONDARY_PHOTO_WIDTH).toInt(),
            ImageProperty.convertDpToPx(applicationContext, SECONDARY_PHOTO_HEIGHT).toInt())
    }


    private fun setUpTextByCoord(x: Float, y: Float) {
        for(element in textElementRectList) {
            val left = newImageProperty!!.normalizeX(element.boundingBox.left.toFloat())
            val right = newImageProperty!!.normalizeX(element.boundingBox.right.toFloat())
            val top = newImageProperty!!.normalizeY(element.boundingBox.top.toFloat())
            val bottom = newImageProperty!!.normalizeY(element.boundingBox.bottom.toFloat())
            if ( (x in left..right) and (y in top..bottom) ) {
                if (currentPageNumber < 5) {
                    pagerAdapter?.update(propertyNameList[currentPageNumber], element.value)
                }
            }
        }
    }


    fun onSubmitButtonClick(view: View) {
        val dataMap = pagerAdapter!!.getDataMap()
        val emptyField = checkForEmptyField(dataMap)

        if(emptyField == 0) {
            val pictureTag = System.currentTimeMillis().toString()
            val data = SimpleDateFormat("dd/M/yyyy hh:mm a", Locale.US)
            val replyIntent = Intent()
            val person = Person(
                dataMap[propertyNameList[0]], dataMap[propertyNameList[2]],
                dataMap[propertyNameList[1]], dataMap[propertyNameList[3]],
                dataMap[propertyNameList[4]], pictureTag ,data.format(Date())
            )
            saveThumbnail(pictureTag)
            replyIntent.putExtra(Person::class.java.simpleName, person)
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }
        else {
            Toast.makeText(applicationContext, "At least one field is empty!", Toast.LENGTH_SHORT).show()
            viewPager?.currentItem = emptyField
        }
    }

    private fun checkForEmptyField(dataMap: HashMap<String, String>) : Int {
        var emptyField = 0
        for (i in 0 until dataMap.size)
            if (dataMap[propertyNameList[i]] == "") { emptyField = i }
        return emptyField
    }




    private fun saveThumbnail(pictureTag: String?) {
        //save file in internal storage
        val handler = Handler()
        if (mFace != null) {
            progressBar?.visibility = ProgressBar.VISIBLE
            Thread(Runnable {
                applicationContext.openFileOutput(pictureTag, Context.MODE_PRIVATE).use {
                    val byteOS = ByteArrayOutputStream()
                    val thumbnail = ImageProperty.getIncreasedThumbnail(mBitMap, mFace)
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, byteOS)
                    it.write(byteOS.toByteArray())
                }

                handler.post {
                    progressBar?.visibility = ProgressBar.INVISIBLE
                }
            }).start()
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBitMap?.recycle()
    }


    inner class MyPageListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            currentPageNumber = position
        }

    }

    fun onBackButtonClick(view: View) {
        if (currentPageNumber > 0) {
            viewPager?.currentItem = currentPageNumber - 1
        }
    }

    fun onNextButtonClick(view: View) {
        if (currentPageNumber < 6) {
            viewPager?.currentItem = currentPageNumber + 1
        }
    }
}
