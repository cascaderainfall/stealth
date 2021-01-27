package com.cosmos.unreddit.util

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.cosmos.unreddit.post.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

object PostUtil {
    fun getProcessedUrl(url: String): String = url.replace("&amp;", "&")

    // TODO: Move somewhere else
    fun filterPosts(posts: Flow<PagingData<PostEntity>>, history: Flow<List<String>>,
                    showNsfw: Flow<Boolean>)
    : Flow<PagingData<PostEntity>> {
        return combine(posts, history, showNsfw) { _posts, _history, _showNsfw ->
            _posts.filter { post ->
                _showNsfw || !post.isOver18
            }.map { post ->
                if (_history.contains(post.id)) {
                    post.seen = true
                }
                post
            }
        }
    }

    fun getAuthorGradientColor(
        context: Context,
        @ColorRes start: Int,
        @ColorRes end: Int
    ): IntArray {
        return intArrayOf(
            ContextCompat.getColor(context, start),
            ContextCompat.getColor(context, end)
        )
    }
}
