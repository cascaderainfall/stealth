package com.cosmos.unreddit.ui.user

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.model.Comment
import com.cosmos.unreddit.data.model.Comment.CommentEntity
import com.cosmos.unreddit.databinding.ItemUserCommentBinding
import com.cosmos.unreddit.ui.common.widget.RedditView
import com.cosmos.unreddit.util.DateUtil
import com.cosmos.unreddit.util.extension.blurText

class UserCommentsAdapter(
    context: Context,
    private val onLinkClickListener: RedditView.OnLinkClickListener? = null,
    private val commentClickListener: CommentClickListener
) : PagingDataAdapter<Comment, UserCommentsAdapter.CommentViewHolder>(COMMENT_COMPARATOR) {

    interface CommentClickListener {
        fun onClick(comment: CommentEntity)

        fun onLongClick(comment: CommentEntity)
    }

    private val colorPrimary by lazy {
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CommentViewHolder(ItemUserCommentBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position) as? CommentEntity ?: return
        holder.bind(comment)
    }

    inner class CommentViewHolder(
        private val binding: ItemUserCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentEntity) {
            binding.comment = comment
            binding.includeItemComment.comment = comment

            with(comment) {
                binding.includeItemComment.commentScore.blurText(scoreHidden)

                with(binding.includeItemComment.commentDate) {
                    val timeDifference = DateUtil.getTimeDifference(context, created)
                    text = if (edited > -1) {
                        val editedTimeDifference = DateUtil.getTimeDifference(
                            context,
                            edited,
                            false
                        )
                        context.getString(
                            R.string.comment_date_edited,
                            timeDifference,
                            editedTimeDifference
                        )
                    } else {
                        timeDifference
                    }
                }

                with(binding.includeItemComment.commentColorIndicator) {
                    visibility = View.VISIBLE
                    backgroundTintList = colorPrimary
                }

                with(binding.includeItemComment.commentFlair) {
                    if (!flair.isEmpty()) {
                        visibility = View.VISIBLE

                        setFlair(flair)
                    } else {
                        visibility = View.GONE
                    }
                }

                with(binding.includeItemComment.commentAwards) {
                    if (awards.isNotEmpty()) {
                        visibility = View.VISIBLE

                        setAwards(awards, totalAwards)
                    } else {
                        visibility = View.GONE
                    }
                }

            }

            with(binding.includeItemComment.commentBody) {
                setText(comment.body)
                setOnLinkClickListener(onLinkClickListener)
                setOnClickListener { commentClickListener.onClick(comment) }
                setOnLongClickListener {
                    commentClickListener.onLongClick(comment)
                    true
                }
            }

            itemView.run {
                setOnClickListener { commentClickListener.onClick(comment) }
                setOnLongClickListener {
                    commentClickListener.onLongClick(comment)
                    true
                }
            }
        }
    }

    companion object {
        private val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {

            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return (oldItem as? CommentEntity)?.name == (newItem as? CommentEntity)?.name
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem as? CommentEntity == newItem as? CommentEntity
            }
        }
    }
}
