package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.details.AboutData
import com.cosmos.unreddit.subreddit.SubredditEntity

object SubredditMapper {

    fun dataToEntity(data: AboutData): SubredditEntity {
        with (data) {
            return SubredditEntity(
                wikiEnabled,
                displayName,
                getHeader(),
                title,
                getPrimaryColor(),
                activeUserCount,
                getIcon(),
                subscribers,
                quarantine,
                publicDescription,
                getKeyColor(),
                getBackgroundColor(),
                over18,
                description,
                url,
                getTimeInMillis()
            )
        }
    }
}