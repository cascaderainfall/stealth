package com.cosmos.unreddit.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import coil.load
import coil.size.Precision
import coil.size.Scale
import com.cosmos.unreddit.R

fun Double.getPercentageValue(start: Int, end: Int) = end * this + start * (1 - this)

fun Activity.setStatusBarColor(color: Int) {
    with(window) {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = color
    }
}

fun View.showSoftKeyboard() {
    requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideSoftKeyboard() {
    requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.loadSubredditIcon(uri: String?) {
    this.load(uri) {
        crossfade(true)
        scale(Scale.FILL)
        precision(Precision.AUTOMATIC)
        placeholder(R.drawable.icon_reddit_placeholder)
        error(R.drawable.icon_reddit_placeholder)
        fallback(R.drawable.icon_reddit_placeholder)
    }
}
