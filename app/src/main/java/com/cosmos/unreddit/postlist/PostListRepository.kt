package com.cosmos.unreddit.postlist

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.pojo.details.AboutChild
import com.cosmos.unreddit.api.pojo.details.AboutUserChild
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.database.RedditDatabase
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.preferences.Preferences
import com.cosmos.unreddit.subreddit.Subscription
import com.cosmos.unreddit.user.CommentsDataSource
import com.cosmos.unreddit.user.History
import com.cosmos.unreddit.user.UserPostsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class PostListRepository private constructor(context: Context) {

    private val redditApi: RedditApi = RedditApi.instance
    private val redditDatabase: RedditDatabase = RedditDatabase.getInstance(context)
    private val preferences: Preferences = Preferences(context)

    fun getPost(permalink: String): Flow<List<Listing>> = flow {
        emit(redditApi.getPost(permalink, 20))
    }

    //region Subreddit

    fun getPosts(subreddit: String, pageSize: Int = DEFAULT_LIMIT): Flow<PagingData<PostEntity>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            PostListDataSource(redditApi, subreddit)
        }.flow
    }

    fun getSubredditInfo(subreddit: String): Flow<AboutChild> = flow { // TODO: Manage errors
        emit(redditApi.getSubredditInfo(subreddit) as AboutChild)
    }

    //endregion

    //region Subscriptions

    fun getSubscriptions(): Flow<List<Subscription>> = redditDatabase.subscriptionDao()
        .getSubscriptions().distinctUntilChanged()

    suspend fun subscribe(name: String, icon: String? = null) {
        redditDatabase.subscriptionDao().insert(Subscription(name, System.currentTimeMillis(), icon))
    }

    suspend fun unsubscribe(name: String) {
        redditDatabase.subscriptionDao().deleteFromName(name)
    }

    //endregion

    //region User

    fun getUserPosts(user: String, pageSize: Int = DEFAULT_LIMIT): Flow<PagingData<PostEntity>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            UserPostsDataSource(redditApi, user)
        }.flow
    }

    fun getUserComments(user: String, pageSize: Int = DEFAULT_LIMIT): Flow<PagingData<Comment>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            CommentsDataSource(redditApi, user)
        }.flow
    }

    fun getUserInfo(user: String): Flow<AboutUserChild> = flow {
        emit(redditApi.getUserInfo(user) as AboutUserChild)
    }

    //endregion

    fun getHistory(): Flow<List<History>> {
        return redditDatabase.historyDao().getHistory()
    }

    suspend fun insertPostInHistory(id: String) {
        redditDatabase.historyDao().upsert(History(id, System.currentTimeMillis()))
    }

    fun getShowNsfw(): Flow<Boolean> {
        return preferences.getShowNsfw()
    }

    companion object {
        private const val DEFAULT_LIMIT = 25

        @Volatile private var INSTANCE: PostListRepository? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PostListRepository(context).also { INSTANCE = it }
        }
    }
}