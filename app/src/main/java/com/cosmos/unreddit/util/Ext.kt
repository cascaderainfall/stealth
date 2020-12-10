package com.cosmos.unreddit.util

import android.app.Activity
import android.view.WindowManager

fun Double.getPercentageValue(start: Int, end: Int) = end * this + start * (1 - this)

fun Activity.setStatusBarColor(color: Int) {
    with(window) {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = color
    }
}