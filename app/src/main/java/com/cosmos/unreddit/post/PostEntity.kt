package com.cosmos.unreddit.post

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.cosmos.unreddit.R
import com.cosmos.unreddit.util.getPercentageValue
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
@Entity(tableName = "saved_posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "subreddit")
    val subreddit: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "ratio")
    val ratio: Double,

    @ColumnInfo(name = "total_awards")
    val totalAwards: Int,

    @ColumnInfo(name = "oc")
    val isOC: Boolean,

    @ColumnInfo(name = "flair")
    val flair: String?,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "type")
    val type: PostType,

    @ColumnInfo(name = "domain")
    val domain: String,

    @ColumnInfo(name = "selftext")
    val selfText: String?,

    @ColumnInfo(name = "selftext_html")
    val selfTextHtml: String?,

    @ColumnInfo(name = "pinned")
    val isPinned: Boolean,

    @ColumnInfo(name = "nsfw")
    val isOver18: Boolean,

    @ColumnInfo(name = "preview_url")
    val preview: String?,

    @Ignore
    val awards: List<Award>,

    @ColumnInfo(name = "spoiler")
    val isSpoiler: Boolean,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "comments")
    val commentsNumber: Int,

    @ColumnInfo(name = "permalink")
    val permalink: String,

    @ColumnInfo(name = "stickied")
    val isStickied: Boolean,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "timestamp")
    val created: Long,

    var seen: Boolean
) : Parcelable {

    constructor(id: String, subreddit: String, title: String, ratio: Double, totalAwards: Int,
                isOC: Boolean, flair: String?, score: Int, type: PostType, domain: String,
                selfText: String?, selfTextHtml: String?, isPinned: Boolean, isOver18: Boolean,
                preview: String?, isSpoiler: Boolean, author: String, commentsNumber: Int,
                permalink: String, isStickied: Boolean, url: String, created: Long, seen: Boolean)
            : this(id, subreddit, title, ratio, totalAwards, isOC, flair, score, type, domain,
        selfText, selfTextHtml, isPinned, isOver18, preview, listOf(), isSpoiler, author,
        commentsNumber, permalink, isStickied, url, created, seen)

    fun getRatioColor(context: Context): Int {
        val low = ContextCompat.getColor(context, R.color.ratio_gradient_low)
        val high = ContextCompat.getColor(context, R.color.ratio_gradient_high)

        val r = ratio.getPercentageValue(Color.red(low), Color.red(high))
        val g = ratio.getPercentageValue(Color.green(low), Color.green(high))
        val b = ratio.getPercentageValue(Color.blue(low), Color.blue(high))

        return Color.rgb(r.roundToInt(), g.roundToInt(), b.roundToInt())
    }

    fun getVoteCount(): String {
        return when {
            score < 1000 -> score.toString()
            else -> {
                val roundedScore = String.format("%.1f", score.div(1000f))
                "${roundedScore}k"
            }
        }
    }
}