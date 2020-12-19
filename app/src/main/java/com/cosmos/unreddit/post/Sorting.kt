package com.cosmos.unreddit.post

import com.cosmos.unreddit.api.RedditApi

data class Sorting(
    val generalSorting: RedditApi.Sort,

    val timeSorting: RedditApi.TimeSorting? = null
)
