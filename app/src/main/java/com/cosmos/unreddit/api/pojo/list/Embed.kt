package com.cosmos.unreddit.api.pojo.list

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Embed (
    @Json(name = "url")
    val url: String?,

    @Json(name = "provider_name")
    val providerName: String
)