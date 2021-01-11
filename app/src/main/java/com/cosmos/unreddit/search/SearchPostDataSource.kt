package com.cosmos.unreddit.search

import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.post.Sorting
import com.cosmos.unreddit.postlist.PostListDataSource

class SearchPostDataSource(
    private val redditApi: RedditApi,
    query: String,
    sorting: Sorting
) : PostListDataSource(redditApi, query, sorting) {

    override suspend fun getResponse(query: String, sorting: Sorting, after: String?): Listing {
        return redditApi.searchPost(query, sorting.generalSorting, sorting.timeSorting, after)
    }
}
