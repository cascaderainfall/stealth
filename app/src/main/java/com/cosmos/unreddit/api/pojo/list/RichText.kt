package com.cosmos.unreddit.api.pojo.list

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RichText(
    @Json(name = "e")
    val e: String,

    @Json(name = "t")
    val t: String?,

    @Json(name = "u")
    val u: String?
)
