package com.cosmos.unreddit.database

import androidx.room.TypeConverter
import com.cosmos.unreddit.post.PostType

class Converters {
    @TypeConverter
    fun fromPostTypeInt(type: Int?): PostType? {
        return type?.let { PostType.toType(it) }
    }

    @TypeConverter
    fun toPostTypeInt(postType: PostType?): Int? {
        return postType?.value
    }
}