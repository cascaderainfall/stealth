package com.cosmos.unreddit.post

interface Comment

data class CommentEntity(
    val totalAwards: Int,

    val flairType: String?,

    val linkId: String,

    val replies: List<Comment>,

    val author: String,

    val score: Int,

    // TODO
    // val awardings: List<Awarding>,

    val body: String,

    val bodyHtml: String,

    val edited: Long,

    val isSubmitter: Boolean,

    val stickied: Boolean,

    val scoreHidden: Boolean,

    val permalink: String,

    val id: String,

    val created: Long,

    val controversiality: Int,

    val flair: String?,

    val depth: Int
) : Comment {
    val hasReplies: Boolean
        get() = replies.isNotEmpty()

    fun getVoteCount(): String {
        return when {
            scoreHidden -> "12345" // TODO: Blur score when hidden
            score < 1000 -> score.toString()
            else -> {
                val roundedScore = String.format("%.1f", score.div(1000f))
                "${roundedScore}k"
            }
        }
    }
}

data class MoreEntity(
    val more: List<String>,

    val depth: Int
) : Comment

fun Comment.getType(): CommentType = when (this) {
    is CommentEntity -> CommentType.COMMENT
    is MoreEntity -> CommentType.MORE
    else -> throw IllegalArgumentException("Unknown type ${javaClass.simpleName}")
}
