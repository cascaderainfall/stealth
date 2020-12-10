package com.cosmos.unreddit.user

import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.postlist.PostListDataSource

class UserPostsDataSource(private val redditApi: RedditApi,
                          query: String)
    : PostListDataSource(redditApi, query) {

    override suspend fun getResponse(query: String, after: String?): Listing {
        return redditApi.getUserPosts(query, after)
    }
}