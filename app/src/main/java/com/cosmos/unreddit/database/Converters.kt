package com.cosmos.unreddit.database

import androidx.room.TypeConverter
import com.cosmos.unreddit.model.PosterType
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

    @TypeConverter
    fun fromPosterTypeInt(type: Int?): PosterType? {
        return type?.let { PosterType.toType(it) }
    }

    @TypeConverter
    fun toPosterTypeInt(posterType: PosterType?): Int? {
        return posterType?.value
    }
}
