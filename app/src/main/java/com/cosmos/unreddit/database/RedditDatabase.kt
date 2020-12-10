package com.cosmos.unreddit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.subreddit.Subscription
import com.cosmos.unreddit.user.History
import com.cosmos.unreddit.util.SingletonHolder

@Database(entities = [PostEntity::class, Subscription::class, History::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class RedditDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun subscriptionDao(): SubscriptionDao

    abstract fun historyDao(): HistoryDao

    companion object : SingletonHolder<RedditDatabase, Context> ({ context ->
        Room.databaseBuilder(context, RedditDatabase::class.java, "reddit_db").build()
    })
}