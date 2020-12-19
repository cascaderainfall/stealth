package com.cosmos.unreddit.postlist

import android.util.Log
import androidx.paging.PagingSource
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.database.PostMapper
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.post.Sorting

open class PostListDataSource(
    private val redditApi: RedditApi,
    private val query: String,
    private val sorting: Sorting
) : PagingSource<String, PostEntity>() {

    override val keyReuseSupported: Boolean = true

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostEntity> {
        return try {
            val response = getResponse(query, sorting, params.key)
            val data = response.data

            val items = PostMapper.dataToEntities(data.children)

            LoadResult.Page(items, data.before, data.after)
        } catch (e: Exception) {
            Log.e("PostListDataSource", "Error", e)
            LoadResult.Error(e)
        }
    }

    open suspend fun getResponse(query: String, sorting: Sorting, after: String?): Listing {
        return redditApi.getSubreddit(query, sorting.generalSorting, sorting.timeSorting, after)
    }
}
