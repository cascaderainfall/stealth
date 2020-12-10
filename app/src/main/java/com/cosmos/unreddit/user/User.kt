package com.cosmos.unreddit.user

data class User(
    val displayName: String,

    val title: String,

    val over18: Boolean,

    val icon: String?,

    val url: String,

    val publicDescription: String,

    val postKarma: Int,

    val commentKarma: Int,

    val created: Long
)
