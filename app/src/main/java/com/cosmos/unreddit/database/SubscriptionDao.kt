package com.cosmos.unreddit.database

import androidx.room.Dao
import androidx.room.Query
import com.cosmos.unreddit.subreddit.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubscriptionDao : BaseDao<Subscription> {

    @Query("DELETE FROM subscription WHERE name = :name")
    abstract suspend fun deleteFromName(name: String)

    @Query("SELECT * FROM subscription")
    abstract fun getSubscriptions(): Flow<List<Subscription>>
}