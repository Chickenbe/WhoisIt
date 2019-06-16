package com.example.anton.idapplication

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.*

abstract class RecyclerClickListener(context: Context) : RecyclerView.OnItemTouchListener {

    private val gestureDetector: GestureDetector
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }
    }

    init {
        gestureDetector = GestureDetector(context, gestureListener)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(e)) {
            val clickedChild = rv.findChildViewUnder(e.x, e.y)
            if (clickedChild != null && !clickedChild.dispatchTouchEvent(e)) {
                val clickedPosition = rv.getChildAdapterPosition(clickedChild)
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    onItemClick(rv, clickedChild, clickedPosition)
                    return true
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    abstract fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int)
}