package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.AboutUserData
import com.cosmos.unreddit.user.User

object UserMapper {

    fun dataToEntity(data: AboutUserData): User {
        with (data) {
            return User(
                name,
                subreddit.title,
                subreddit.over18,
                iconImg,
                subreddit.url,
                subreddit.publicDescription,
                linkKarma,
                commentKarma,
                getTimeInMillis()
            )
        }
    }
}