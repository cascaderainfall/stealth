package com.cosmos.unreddit.api.pojo.details

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MoreData (
    @Json(name = "count")
    val count: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "depth")
    val depth: Int?,

    @Json(name = "children")
    val children: List<String>
)
