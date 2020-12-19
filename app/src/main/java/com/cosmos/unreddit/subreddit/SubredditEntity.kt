package com.cosmos.unreddit.subreddit

data class SubredditEntity (
    val wikiEnabled: Boolean,

    val displayName: String,

    val header: String?,

    val title: String,

    val primaryColor: Int,

    val activeUserCount: Int,

    val icon: String?,

    val subscribers: Int,

    val quarantine: Boolean,

    val publicDescription: String,

    val keyColor: Int,

    val backgroundColor: Int,

    val over18: Boolean,

    val description: String,

    val url: String,

    val created: Long
) {
    fun getSubscribersCount(): String {
        return when {
            subscribers < 1_000_000 -> subscribers.toString()
            else -> {
                val roundedSubscribers = String.format("%.1f", subscribers.div(1_000_000f))
                "${roundedSubscribers}m"
            }
        }
    }

    fun getActiveUsers(): String {
        return when {
            activeUserCount < 1000 -> activeUserCount.toString()
            else -> {
                val roundedUsers = String.format("%.1f", activeUserCount.div(1000f))
                "${roundedUsers}k"
            }
        }
    }
}