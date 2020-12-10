package com.cosmos.unreddit.postlist

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.database.RedditDatabase
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.preferences.Preferences
import com.cosmos.unreddit.subreddit.Subscription
import com.cosmos.unreddit.user.History
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class PostListRepository private constructor(context: Context) {

    private val redditApi: RedditApi = RedditApi.instance
    private val redditDatabase: RedditDatabase = RedditDatabase.getInstance(context)
    private val preferences: Preferences = Preferences(context)

    //region Subreddit

    fun getPosts(subreddit: String, pageSize: Int = DEFAULT_LIMIT): Flow<PagingData<PostEntity>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            PostListDataSource(redditApi, subreddit)
        }.flow
    }

    //endregion

    //region Subscriptions

    fun getSubscriptions(): Flow<List<Subscription>> = redditDatabase.subscriptionDao()
        .getSubscriptions().distinctUntilChanged()

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