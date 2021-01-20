package com.cosmos.unreddit.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.cosmos.unreddit.R
import com.cosmos.unreddit.util.hideSoftKeyboard
import com.cosmos.unreddit.util.showSoftKeyboard
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.MaterialFadeThrough

class SearchInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private val searchInputTransition by lazy {
        TransitionSet().apply {
            addTransition(Fade(Fade.OUT))
            addTransition(Slide(Gravity.END))
            addTransition(Fade(Fade.IN))
            duration = 250
            addTarget(this@SearchInputEditText)
        }
    }

    private val appBarTransition by lazy {
        MaterialFadeThrough().apply {
            duration = 500
        }
    }

    private val transitionSet by lazy {
        TransitionSet().apply {
            addTransition(searchInputTransition)
            addTransition(appBarTransition)
        }
    }

    fun show(sceneRoot: ViewGroup, show: Boolean, hideViews: () -> Unit) {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
        hideViews()
        if (show) {
            visibility = View.VISIBLE
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            showSoftKeyboard()
        } else {
            visibility = View.GONE
            isFocusable = false
            isFocusableInTouchMode = false
            hideSoftKeyboard()
        }
    }

    fun addTarget(target: View) {
        appBarTransition.addTarget(target)
    }
}
