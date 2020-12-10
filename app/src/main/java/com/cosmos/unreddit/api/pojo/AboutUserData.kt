package com.cosmos.unreddit.api.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AboutUserData(
    @Json(name = "is_employee")
    val isEmployee: Boolean,

    @Json(name = "is_friend")
    val isFriend: Boolean,

    @Json(name = "subreddit")
    val subreddit: Subreddit,

    @Json(name = "id")
    val id: String,

    @Json(name = "icon_img")
    val iconImg: String,

    @Json(name = "link_karma")
    val linkKarma: Int,

    @Json(name = "total_karma")
    val totalKarma: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "created_utc")
    val created: Long,

    @Json(name = "snoovatar_img")
    val snoovatarImg: String,

    @Json(name = "comment_karma")
    val commentKarma: Int
) {
    fun getTimeInMillis(): Long { // TODO: Remove from all *Data class and put in Util class
        return TimeUnit.SECONDS.toMillis(created)
    }
}
