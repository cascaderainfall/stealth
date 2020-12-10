package com.cosmos.unreddit.api.pojo.details

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Listing (
    @Json(name = "kind")
    val kind: String,

    @Json(name = "data")
    val data: ListingData
)