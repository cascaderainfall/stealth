package com.cosmos.unreddit.data.remote.api.streamable.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoFile(
    @Json(name = "url")
    val url: String,

    @Json(name = "size")
    val size: Int,

    @Json(name = "duration")
    val duration: Double
)
