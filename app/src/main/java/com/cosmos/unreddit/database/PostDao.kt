package com.cosmos.unreddit.database

import androidx.room.Dao
import com.cosmos.unreddit.post.PostEntity

@Dao
abstract class PostDao : BaseDao<PostEntity> {
}