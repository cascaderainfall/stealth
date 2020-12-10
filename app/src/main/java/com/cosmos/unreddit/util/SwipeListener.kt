package com.cosmos.unreddit.util

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class SwipeListener(private val callback: Callback? = null)
    : GestureDetector.SimpleOnGestureListener() {

    interface Callback {
        fun onSwipeLeft()

        fun onSwipeRight()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val distanceX = e2.x - e1.x
        val distanceY = e2.y - e1.y

        if (abs(distanceX) > abs(distanceY)
            && abs(distanceX) > DISTANCE_THRESHOLD
            && abs(velocityX) > VELOCITY_THRESHOLD) {
            if (distanceX > 0) {
                callback?.onSwipeRight()
            } else {
                callback?.onSwipeLeft()
            }
            return true
        }

        return super.onFling(e1, e2, velocityX, velocityY)
    }

    companion object {
        private const val DISTANCE_THRESHOLD = 100
        private const val VELOCITY_THRESHOLD = 100
    }
}