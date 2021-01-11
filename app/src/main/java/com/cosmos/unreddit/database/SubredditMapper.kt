package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.details.AboutChild
import com.cosmos.unreddit.api.pojo.details.AboutData
import com.cosmos.unreddit.api.pojo.details.Child
import com.cosmos.unreddit.api.pojo.details.ChildType
import com.cosmos.unreddit.subreddit.SubredditEntity

object SubredditMapper {

    fun dataToEntity(data: AboutData): SubredditEntity {
        with(data) {
            return SubredditEntity(
                wikiEnabled ?: false,
                displayName,
                getHeader(),
                title,
                getPrimaryColor(),
                activeUserCount,
                getIcon(),
                subscribers,
                quarantine ?: false,
                publicDescription,
                getKeyColor(),
                getBackgroundColor(),
                over18 ?: false,
                description,
                url,
                getTimeInMillis()
            )
        }
    }

    fun dataToEntities(data: List<Child>?): List<SubredditEntity> {
        val subredditList = mutableListOf<SubredditEntity>()

        data?.forEach {
            if (it.kind == ChildType.t5) {
                subredditList.add(dataToEntity((it as AboutChild).data))
            }
        }

        return subredditList
    }
}
