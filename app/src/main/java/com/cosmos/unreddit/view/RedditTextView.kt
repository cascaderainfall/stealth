package com.cosmos.unreddit.view

import android.content.Context
import android.text.util.Linkify
import android.text.util.Linkify.TransformFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.cosmos.unreddit.util.RedditMarkwon
import com.cosmos.unreddit.util.RedditUri
import java.util.regex.Pattern


class RedditTextView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {

    private var scrollEnabled: Boolean = true

    fun setText(text: String) {
        setText(text, true)
    }

    fun setText(text: String, enableScroll: Boolean) {
        scrollEnabled = enableScroll
        setMarkdownText(text)
        linkify()
    }

    private fun setMarkdownText(text: String) {
        RedditMarkwon.getInstance(context).setMarkdown(this, text)
    }

    private fun linkify() {
        Linkify.addLinks(this, REDDIT_POST_LINK_PATTERN, RedditUri.SUBREDDIT_URI.toString(),
            null, REDDIT_LINK_FILTER)
        Linkify.addLinks(this, REDDIT_USER_LINK_PATTERN, RedditUri.USER_URI.toString(), null,
            REDDIT_LINK_FILTER)
    }

    override fun scrollTo(x: Int, y: Int) {
        if (scrollEnabled) {
            super.scrollTo(x, y)
        }
    }

    companion object {
        // TODO: Match whole word ex: https://www.reddit.com/r/TrueOffMyChest/comments/k36kft/as_a_member_of_the_lgbtq_community_i_find/
        private val REDDIT_POST_LINK_PATTERN = Pattern.compile("/?r(/(\\w|\\d){1,20})")
        private val REDDIT_USER_LINK_PATTERN = Pattern.compile("/?u(/(\\w|\\d){1,20})")

        private val REDDIT_LINK_FILTER = TransformFilter { match, _ ->
            match.group(1)
        }
    }
}