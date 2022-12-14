package com.cosmos.unreddit.data.remote.api.streamable.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Files(
    @Json(name = "mp4")
    val mp4: VideoFile
)
