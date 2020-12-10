package com.cosmos.unreddit.postdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.databinding.ItemCommentBinding
import com.cosmos.unreddit.databinding.ItemMoreBinding
import com.cosmos.unreddit.databinding.ItemReplyBinding
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.CommentEntity
import com.cosmos.unreddit.post.MoreEntity

class CommentAdapter(private val comment: Comment)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // TODO: Add comment count when collapsed

    // Expand all comments with a depth < 3
    private var isExpanded: Boolean = comment is CommentEntity && comment.depth < 3

    private val onCommentClickListener = View.OnClickListener {
        val newExpandedValue = !isExpanded
        isExpanded = newExpandedValue
        if (newExpandedValue) {
            notifyItemRangeInserted(1, (comment as CommentEntity).replies.size)
            //To update the header expand icon
            notifyItemChanged(0)
        } else {
            notifyItemRangeRemoved(1, (comment as CommentEntity).replies.size)
            //To update the header expand icon
            notifyItemChanged(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            Type.COMMENT.value ->
                CommentViewHolder(ItemCommentBinding.inflate(inflater, parent, false))
            Type.REPLY.value ->
                ReplyViewHolder(ItemReplyBinding.inflate(inflater, parent, false))
            Type.MORE.value ->
                MoreViewHolder(ItemMoreBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            when (comment) {
                is CommentEntity -> Type.COMMENT.value
                is MoreEntity ->  Type.MORE.value
                else -> throw IllegalArgumentException("Unknown type")
            }
        } else {
            if (getItem(position) is CommentEntity) {
                Type.REPLY.value
            } else {
                Type.MORE.value
            }
        }
    }

    override fun getItemCount(): Int {
        return when {
            comment is MoreEntity -> {
                1
            }
            isExpanded -> {
                (comment as CommentEntity).replies.size + 1
            }
            else -> {
                1
            }
        }
    }

    private fun getItem(position: Int): Comment? {
        return (comment as CommentEntity).replies.getOrNull(position - 1)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            // Comment is only used for first item
            Type.COMMENT.value ->
                (holder as CommentViewHolder).bind(comment as CommentEntity, onCommentClickListener)
            Type.REPLY.value ->
                (holder as ReplyViewHolder).bind(getItem(position)!!)
            Type.MORE.value -> {
                // More can be first item or part of a reply
                val isFirst = (position == 0)
                val comment: MoreEntity = if (isFirst) {
                    comment as MoreEntity
                } else {
                    getItem(position) as MoreEntity
                }
                (holder as MoreViewHolder).bind(comment, isFirst)
            }
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    private enum class Type(val value: Int) {
        COMMENT(0), REPLY(1), MORE(2)
    }
}