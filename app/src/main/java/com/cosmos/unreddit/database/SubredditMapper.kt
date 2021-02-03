package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.details.AboutChild
import com.cosmos.unreddit.api.pojo.details.AboutData
import com.cosmos.unreddit.api.pojo.details.Child
import com.cosmos.unreddit.api.pojo.details.ChildType
import com.cosmos.unreddit.parser.HtmlParser
import com.cosmos.unreddit.subreddit.SubredditEntity

object SubredditMapper {

    suspend fun dataToEntity(
        data: AboutData,
        htmlParser: HtmlParser = HtmlParser()
    ): SubredditEntity {
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
                htmlParser.separateHtmlBlocks(publicDescriptionHtml),
                getKeyColor(),
                getBackgroundColor(),
                over18 ?: false,
                htmlParser.separateHtmlBlocks(descriptionHtml),
                url,
                getTimeInMillis()
            )
        }
    }

    suspend fun dataToEntities(data: List<Child>?): List<SubredditEntity> {
        val subredditList = mutableListOf<SubredditEntity>()

        val htmlParser = HtmlParser()

        data?.forEach {
            if (it.kind == ChildType.t5) {
                subredditList.add(dataToEntity((it as AboutChild).data, htmlParser))
            }
        }

        return subredditList
    }
}
