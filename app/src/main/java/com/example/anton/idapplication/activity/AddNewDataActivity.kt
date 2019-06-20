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
import java.lang.StringBuilder
import com.google.android.gms.vision.text.TextBlock
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.toRectF
import com.google.android.gms.vision.text.Text


private val textElementRectList: MutableList<Text> = mutableListOf()
private var PRIMARY_PHOTO_WIDTH: Int = 0
private var PRIMARY_PHOTO_HEIGHT: Int = 0
private var SECONDARY_PHOTO_WIDTH: Int = 0
private var SECONDARY_PHOTO_HEIGHT: Int = 0


class AddNewDataActivity : AppCompatActivity() {
    var imageViewSecondary: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_data)
        supportActionBar!!.hide()                       //hides action bar

        val imageUri = intent.getStringExtra("imageURI")
        val imageViewPrimary = findViewById<ImageView>(R.id.imageViewPrimary)
        val recognizeButton = findViewById<Button>(R.id.recognize_button)
        val fileNameTextView = findViewById<TextView>(R.id.file_name_textView)
        val replyIntent = Intent()

        if (TextUtils.isEmpty(imageUri)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        } else {
            val uri: Uri = imageUri.toUri()
            val bitMap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            PRIMARY_PHOTO_HEIGHT = bitMap.height
            PRIMARY_PHOTO_WIDTH = bitMap.width
            imageViewPrimary.setImageBitmap(bitMap)
            imageViewPrimary.setBackgroundResource(R.drawable.back)
            recognizeButton.setOnClickListener { initRecognizeButtonClickListener(bitMap, fileNameTextView, imageViewPrimary) }
            fileNameTextView.text = imageUri
            setResult(Activity.RESULT_OK, replyIntent)
        }
    }

    private fun initRecognizeButtonClickListener(bitMap: Bitmap, fileNameTextView: TextView,
                                                 imageViewPrimary: ImageView) {
        val textRecognizer: TextRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (!textRecognizer.isOperational) {
            fileNameTextView.text = "Error"
        } else {
            val frame: Frame = Frame.Builder().setBitmap(bitMap).build()
            val items = textRecognizer.detect(frame)
            val strBuilder = StringBuilder()

            for (i in 0 until items.size()) {
                val item = items.valueAt(i) as TextBlock

                Log.e("Rect coord", "Coord is: ${item.boundingBox.left}, ${item.boundingBox.top}, ${item.boundingBox.right}, ${item.boundingBox.bottom}")
                strBuilder.append(item.value)
                strBuilder.append("/")
                for(line in item.components) {
                    textElementRectList.add(line)
                }
            }

            fileNameTextView.text = strBuilder.toString()
        }
        setUpNewImageView(imageViewPrimary, bitMap)

    }


    private fun setUpNewImageView(imageViewPrimary: ImageView, bitMap: Bitmap) {
        val layOutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val mainLayOut = findViewById<FrameLayout>(R.id.frameLayoutID)
        mainLayOut.removeView(imageViewPrimary)
        layOutParams.topMargin = convertDpToPx(applicationContext, 5F).toInt()
        if (imageViewSecondary != null) { mainLayOut.removeView(imageViewSecondary) }
        imageViewSecondary = MyImageView(applicationContext).apply {
            layoutParams = layOutParams
            scaleType = ImageView.ScaleType.FIT_XY
            setImageBitmap(bitMap)
            setPadding(0,0,0,0)
            setBackgroundResource(R.drawable.back)
            layOutParams.width = convertDpToPx(applicationContext, 355F).toInt()
            layOutParams.height = convertDpToPx(applicationContext, 500F).toInt()
            Log.e("IMAGE SOURCE #1", "Image: width -> ${layOutParams.width}, height -> ${layOutParams.height}")
        }
        mainLayOut.addView(imageViewSecondary)
        SECONDARY_PHOTO_WIDTH = imageViewSecondary!!.layoutParams.width
        SECONDARY_PHOTO_HEIGHT = imageViewSecondary!!.layoutParams.height
        Log.e("IMAGE SOURCE #2", "Image: width -> ${bitMap.width}, height -> ${bitMap.height}")
        Log.e("IMAGE VIEW SOURCE", "View: width -> ${imageViewSecondary!!.layoutParams.width}, height -> ${imageViewSecondary!!.layoutParams.height}")

    }


    private fun convertDpToPx(context: Context, dp: Float): Float = dp * context.resources.displayMetrics.density

}


class MyImageView(context: Context) : ImageView(context) {

    var rectPaint = Paint()
    var textPaint = Paint()

    init {
        rectPaint.color = Color.WHITE
        rectPaint.strokeWidth = 4.0F
        rectPaint.style = Paint.Style.STROKE

        textPaint.color = Color.WHITE
        textPaint.textSize = 54.0F
        if (textElementRectList.size == 0) Toast.makeText(context, "There is no item found", Toast.LENGTH_SHORT).show()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var lineCount = 0
        for (line in textElementRectList) {
            val rect: RectF = line.boundingBox.toRectF()
            rect.apply {
                left = normalizeX(left)
                top = normalizeY(top)
                right = normalizeX(right)// - (normalizeX(right) / 100) * 30
                bottom = normalizeY(bottom)
            }

            canvas!!.drawRect(rect, rectPaint)
            canvas.drawText("${lineCount++}: ${line.value}", rect.left, rect.bottom, textPaint)

            Log.e("ss",
                "Coordinates for rectangle: ${rect.left}, ${rect.top}, ${rect.right}, " +
                        "${rect.bottom}, list size = ${textElementRectList.size}")
        }
        textElementRectList.clear()
    }


    private fun normalizeX(x: Float) : Float {
        val normalizeX: Float = x / (PRIMARY_PHOTO_WIDTH - x)
        Log.e("normal", "Normalize x is ${normalizeX * SECONDARY_PHOTO_WIDTH}")
        return normalizeX * SECONDARY_PHOTO_WIDTH
    }

    private fun normalizeY(y: Float) : Float{
        val normalizeY: Float = y / (PRIMARY_PHOTO_HEIGHT)
        Log.e("normal", "Normalize y is ${normalizeY * SECONDARY_PHOTO_HEIGHT}")
        return normalizeY * SECONDARY_PHOTO_HEIGHT
    }
}
