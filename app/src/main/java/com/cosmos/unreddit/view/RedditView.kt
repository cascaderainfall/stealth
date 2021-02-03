package com.cosmos.unreddit.view

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import com.cosmos.unreddit.parser.HtmlBlock
import com.cosmos.unreddit.parser.RedditText
import com.cosmos.unreddit.parser.TableBlock
import com.cosmos.unreddit.parser.TextBlock

class RedditView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(
        context,
        GestureListener()
    )

    private val childParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

    init {
        orientation = VERTICAL
        isFocusable = true
        isClickable = true
    }

    fun setText(redditText: RedditText, linkMovementMethod: LinkMovementMethod? = null) {
        removeAllViews()

        val blocks = redditText.blocks
        for (block in blocks) {
            when (block.type) {
                HtmlBlock.BlockType.TEXT -> {
                    addText(block.block as TextBlock, linkMovementMethod)
                }
                HtmlBlock.BlockType.CODE -> {
                    addCode(block.block as TextBlock)
                }
                HtmlBlock.BlockType.TABLE -> {
                    addTable(block.block as TableBlock)
                }
            }
        }
    }

    fun setPreviewText(textBlock: TextBlock, linkMovementMethod: LinkMovementMethod? = null) {
        removeAllViews()
        addText(textBlock, linkMovementMethod)
    }

    fun setTextColor(@ColorInt color: Int) {
        for (child in children) {
            if (child is RedditTextView) {
                child.setTextColor(color)
            }
        }
    }

    private fun addText(textBlock: TextBlock, linkMovementMethod: LinkMovementMethod? = null) {
        val redditTextView = RedditTextView(context).apply {
            layoutParams = childParams
            text = textBlock.text
            if (linkMovementMethod != null) {
                movementMethod = linkMovementMethod
            }
        }
        addView(redditTextView)
    }

    private fun addCode(codeBlock: TextBlock) {
        val redditTextView = RedditTextView(context).apply {
            layoutParams = childParams
            text = codeBlock.text
        }
        addView(wrapWithScrollView(redditTextView))
    }

    private fun addTable(tableBlock: TableBlock) {
        addView(wrapWithScrollView(tableBlock.getTableLayout(context)))
    }

    private fun wrapWithScrollView(view: View): View {
        return HorizontalScrollView(context).apply {
            layoutParams = childParams
            overScrollMode = OVER_SCROLL_NEVER
            scrollBarSize = 0
            addView(view)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            performClick()
            return true
        }
    }
}
