package com.cosmos.unreddit.util

import android.content.Context
import android.text.style.SuperscriptSpan
import android.text.util.Linkify
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import io.noties.markwon.simple.ext.SimpleExtPlugin

object RedditMarkwon : SingletonHolder<Markwon, Context> ({ context ->
    Markwon.builder(context)
        .usePlugin(RedditPlugin())
        .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(SimpleExtPlugin.create {
            // TODO: Not working
            it.addExtension(1, '^') { _, _ -> SuperscriptSpan() }
        })
        .build()
})