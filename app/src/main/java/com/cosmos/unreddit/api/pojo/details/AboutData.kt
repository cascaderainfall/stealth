package com.cosmos.unreddit.api.pojo.details

import android.graphics.Color
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.concurrent.TimeUnit


@JsonClass(generateAdapter = true)
data class AboutData (
    @Json(name = "wiki_enabled")
    val wikiEnabled: Boolean,

    @Json(name = "display_name")
    val displayName: String,

    @Json(name = "header_img")
    val headerImg: String?,

    @Json(name = "title")
    val title: String,

    @Json(name = "primary_color")
    val primaryColor: String,

    @Json(name = "active_user_count")
    val activeUserCount: Int,

    @Json(name = "icon_img")
    val iconImg: String?,

    @Json(name = "accounts_active")
    val activeAccounts: Int,

    @Json(name = "subscribers")
    val subscribers: Int,

    @Json(name = "quarantine")
    val quarantine: Boolean,

    @Json(name = "public_description")
    val publicDescription: String,

    @Json(name = "community_icon")
    val communityIcon: String,

    @Json(name = "banner_background_image")
    val bannerBackgroundImage: String,

    @Json(name = "key_color")
    val keyColor: String,

    @Json(name = "banner_background_color")
    val bannerBackgroundColor: String,

    @Json(name = "over18")
    val over18: Boolean,

    @Json(name = "description")
    val description: String,

    @Json(name = "url")
    val url: String,

    @Json(name = "created_utc")
    val created: Long
) {
    fun getIcon(): String? {
        return when {
            communityIcon.isNotEmpty() -> communityIcon
            else -> iconImg
        }
    }

    fun getHeader(): String? {
        return when {
            bannerBackgroundImage.isNotEmpty() -> bannerBackgroundImage
            else -> headerImg
        }
    }

    fun getTimeInMillis(): Long {
        return TimeUnit.SECONDS.toMillis(created)
    }

    fun getPrimaryColor(): Int {
        return when {
            primaryColor.isNotEmpty() -> Color.parseColor(primaryColor)
            keyColor.isNotEmpty() -> Color.parseColor(keyColor)
            bannerBackgroundColor.isNotEmpty() -> Color.parseColor(bannerBackgroundColor)
            else -> Color.BLUE //TODO("Get app theme color")
        }
    }

    fun getKeyColor(): Int {
        return when {
            keyColor.isNotEmpty() -> Color.parseColor(keyColor)
            primaryColor.isNotEmpty() -> Color.parseColor(primaryColor)
            bannerBackgroundColor.isNotEmpty() -> Color.parseColor(bannerBackgroundColor)
            else -> Color.BLUE //TODO("Get app theme color")
        }
    }

    fun getBackgroundColor(): Int {
        return when {
            bannerBackgroundColor.isNotEmpty() -> Color.parseColor(bannerBackgroundColor)
            primaryColor.isNotEmpty() -> Color.parseColor(primaryColor)
            keyColor.isNotEmpty() -> Color.parseColor(keyColor)
            else -> Color.BLUE //TODO("Get app theme color")
        }
    }
}
