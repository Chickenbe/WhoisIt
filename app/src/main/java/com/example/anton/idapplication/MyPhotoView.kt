package com.example.anton.idapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.loader.content.Loader
import com.example.anton.idapplication.utils.Circle
import com.github.chrisbanes.photoview.PhotoView

class MyPhotoView(context: Context?, attrs: AttributeSet?) : PhotoView(context, attrs) {


    private var landmarksList = emptyList<Circle>()
    private var face: RectF = RectF()
    private var rectPaint: Paint = Paint()
    private var revalidateType = ""
    private var faceCenter = PointF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        rectPaint.color = Color.YELLOW
        rectPaint.strokeWidth = 7.0F
        rectPaint.style = Paint.Style.STROKE


        when(revalidateType) {
            "landmark" -> {
                for (circle in landmarksList) {
                    canvas!!.drawCircle(circle.x, circle.y, 5f, rectPaint)
                }
            }
            "face" -> {
                canvas!!.drawRect(face, rectPaint)
            }
            "face center" -> {
                canvas!!.drawCircle(faceCenter.x, faceCenter.y, 10f, rectPaint)
                canvas!!.drawRect(face, rectPaint)
            }
        }


    }

    fun revalidate(landmarksList: List<Circle>) {
        revalidateType = "landmark"
        this.landmarksList = landmarksList
        this.invalidate()
    }

    fun revalidate(face: RectF) {
        revalidateType = "face"
        this.face = face
        this.invalidate()
    }

    fun revalidate(faceCenter: PointF, face: RectF) {
        revalidateType = "face center"
        this.faceCenter = faceCenter
        this.face = face
        this.invalidate()
    }

}