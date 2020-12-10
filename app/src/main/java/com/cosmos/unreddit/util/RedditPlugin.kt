package com.cosmos.unreddit.util

import android.util.Log
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory

class RedditPlugin : AbstractMarkwonPlugin() {

    /**
     * @see io.noties.markwon.LinkResolver
     * @see io.noties.markwon.core.spans.LinkSpan
     * @see io.noties.markwon.core.factory.LinkSpanFactory
     * @see io.noties.markwon.core.factory.LinkSpanFactory.getSpans
     */
    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        // TODO: Set custom link span to handle long click
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.linkResolver { _, link ->
            // TODO: Implement click
            Log.e("RedditPlugin", link)
        }
    }
}