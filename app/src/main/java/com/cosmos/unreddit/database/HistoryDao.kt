package com.cosmos.unreddit.database

import androidx.room.Dao
import androidx.room.Query
import com.cosmos.unreddit.user.History
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao : BaseDao<History> {

    @Query("SELECT * FROM history")
    abstract fun getHistory(): Flow<List<History>>
}