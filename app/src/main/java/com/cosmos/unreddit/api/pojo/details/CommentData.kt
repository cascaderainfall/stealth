package com.cosmos.unreddit.api.pojo.details

import com.cosmos.unreddit.api.Edited
import com.cosmos.unreddit.api.Replies
import com.cosmos.unreddit.api.pojo.list.Awarding
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
class CommentData(
    @Json(name = "total_awards_received")
    val totalAwards: Int,

    @Json(name = "author_flair_type")
    val flairType: String?,

    @Json(name = "link_id")
    val linkId: String,

    @Json(name = "replies")
    @Replies
    val replies: Listing?,

    @Json(name = "author")
    val author: String,

    @Json(name = "score")
    val score: Int,

    @Json(name = "over_18")
    val isOver18: Boolean,

    @Json(name = "all_awardings")
    val awardings: List<Awarding>,

    @Json(name = "body")
    val body: String,

    @Json(name = "body_html")
    val bodyHtml: String,

    @Json(name = "edited")
    @Edited
    val edited: Long,

    @Json(name = "is_submitter")
    val isSubmitter: Boolean,

    @Json(name = "stickied")
    val stickied: Boolean,

    @Json(name = "score_hidden")
    val scoreHidden: Boolean,

    @Json(name = "permalink")
    val permalink: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "created_utc")
    val created: Long,

    @Json(name = "controversiality")
    val controversiality: Int,

    @Json(name = "author_flair_text")
    val flair: String?,

    @Json(name = "depth")
    val depth: Int?
) {
    fun getTimeInMillis(): Long {
        return TimeUnit.SECONDS.toMillis(created)
    }
}
