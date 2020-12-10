package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.details.Child
import com.cosmos.unreddit.api.pojo.details.ChildType
import com.cosmos.unreddit.api.pojo.details.PostChild
import com.cosmos.unreddit.api.pojo.list.PostData
import com.cosmos.unreddit.post.Award
import com.cosmos.unreddit.post.PostEntity

object PostMapper {

    fun dataToEntity(data: PostData): PostEntity {
        with (data) {
            return PostEntity(
                name,
                prefixedSubreddit,
                title,
                ratio,
                totalAwards,
                isOC,
                flair,
                score,
                getPostType(),
                domain,
                selfText,
                selfTextHtml,
                isPinned,
                isOver18,
                getPreviewUrl(),
                awardings.map { Award(it.count, it.getIcon()) },
                isSpoiler,
                author,
                commentsNumber,
                permalink,
                isStickied,
                url,
                getTimeInMillis(),
                false
            )
        }
    }

    fun dataToEntities(data: List<Child>?): List<PostEntity> {
        val postList = mutableListOf<PostEntity>()

        data?.forEach {
            if (it.kind == ChildType.t3) {
                postList.add(dataToEntity((it as PostChild).data))
            }
        }

        return postList
    }
}