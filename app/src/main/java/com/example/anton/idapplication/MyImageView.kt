package com.example.anton.idapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.widget.ImageView
import androidx.core.graphics.toRectF
import com.example.anton.idapplication.utils.ImageProperty
import com.google.android.gms.vision.text.Text

@SuppressLint("ViewConstructor")
class MyImageView(
    context: Context,
    private val imageProperty: ImageProperty?,
    private val textElementRectList: MutableList<Text>
) : ImageView(context) {

    private var rectPaint = Paint()

    init {
        rectPaint.color = Color.WHITE
        rectPaint.strokeWidth = 4.0F
        rectPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (line in textElementRectList) {
            val rect: RectF = line.boundingBox.toRectF()
            rect.apply {
                left = imageProperty!!.normalizeX(left)
                top = imageProperty.normalizeY(top)
                right = imageProperty.normalizeX(right)
                bottom = imageProperty.normalizeY(bottom)
            }
            canvas!!.drawRect(rect, rectPaint)
        }
    }
}