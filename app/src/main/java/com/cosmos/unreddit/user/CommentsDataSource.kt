package com.cosmos.unreddit.user

import android.util.Log
import androidx.paging.PagingSource
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.database.CommentMapper
import com.cosmos.unreddit.post.Comment

class CommentsDataSource(private val redditApi: RedditApi,
                         private val user: String)
    : PagingSource<String, Comment>() {

    override val keyReuseSupported: Boolean = true

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Comment> {
        return try {
            val response = redditApi.getUserComments(user, params.key)
            val data = response.data

            val items = CommentMapper.dataToEntities(data.children)

            LoadResult.Page(items, data.before, data.after)
        } catch (e: Exception) {
            Log.e("CommentsDataSource", "Error", e)
            LoadResult.Error(e)
        }
    }
}