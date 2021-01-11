package com.cosmos.unreddit.post

import android.os.Parcelable
import com.cosmos.unreddit.api.RedditApi
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sorting(
    val generalSorting: RedditApi.Sort,

    val timeSorting: RedditApi.TimeSorting? = null
) : Parcelable
