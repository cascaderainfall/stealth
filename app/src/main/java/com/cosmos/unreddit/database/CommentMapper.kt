package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.details.*
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.CommentEntity
import com.cosmos.unreddit.post.MoreEntity

object CommentMapper {

    fun dataToEntity(data: CommentData): CommentEntity {
        with (data) {
            return CommentEntity(
                totalAwards,
                flairType,
                linkId,
                dataToEntities(replies?.data?.children),
                author,
                score,
                body,
                bodyHtml,
                edited,
                isSubmitter,
                stickied,
                scoreHidden,
                permalink,
                name,
                getTimeInMillis(),
                controversiality,
                flair,
                depth ?: 0
            )
        }
    }

    fun dataToEntity(data: MoreData): MoreEntity {
        with (data) {
            return MoreEntity(
                children,
                depth ?: 0
            )
        }
    }

    fun dataToEntities(data: List<Child>?): List<Comment> {
        val postList = mutableListOf<Comment>()

        data?.forEach {
            postList.add(
                when (it.kind) {
                    ChildType.t1 -> dataToEntity((it as CommentChild).data)
                    ChildType.more -> dataToEntity((it as MoreChild).data)
                    else -> return@forEach
            })
        }

        return postList
    }
}