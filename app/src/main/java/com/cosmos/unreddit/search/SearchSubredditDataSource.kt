package com.cosmos.unreddit.search

import android.util.Log
import androidx.paging.PagingSource
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.database.SubredditMapper
import com.cosmos.unreddit.post.Sorting
import com.cosmos.unreddit.subreddit.SubredditEntity

class SearchSubredditDataSource(
    private val redditApi: RedditApi,
    private val query: String,
    private val sorting: Sorting
) : PagingSource<String, SubredditEntity>() {

    override val keyReuseSupported: Boolean = true

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SubredditEntity> {
        return try {
            val response = redditApi.searchSubreddit(
                query,
                sorting.generalSorting,
                sorting.timeSorting,
                params.key
            )
            val data = response.data

            val items = SubredditMapper.dataToEntities(data.children)

            LoadResult.Page(items, data.before, data.after)
        } catch (e: Exception) {
            Log.e("SubredditDataSource", "Error", e)
            LoadResult.Error(e)
        }
    }
}