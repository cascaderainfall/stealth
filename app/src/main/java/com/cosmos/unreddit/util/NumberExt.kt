package com.cosmos.unreddit.util

import android.content.Context
import android.util.TypedValue

fun Context.toPixels(value: Number, unit: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
    return TypedValue.applyDimension(unit, value.toFloat(), resources.displayMetrics)
}
