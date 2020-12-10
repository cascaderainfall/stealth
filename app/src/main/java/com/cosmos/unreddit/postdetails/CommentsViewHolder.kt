package com.cosmos.unreddit.postdetails

import android.graphics.BlurMaskFilter
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.databinding.ItemCommentBinding
import com.cosmos.unreddit.databinding.ItemMoreBinding
import com.cosmos.unreddit.databinding.ItemReplyBinding
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.CommentEntity
import com.cosmos.unreddit.post.MoreEntity

class CommentViewHolder(private val binding: ItemCommentBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentEntity, listener: View.OnClickListener) {
        binding.comment = comment

        with(comment) {
            with(binding.commentScore) {
                // Blur score when hidden
                if (scoreHidden) {
                    val radius = textSize / 3
                    val blurMaskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    paint.maskFilter = blurMaskFilter
                } else {
                    paint.maskFilter = null
                }
            }

            if (hasReplies) {
                itemView.setOnClickListener(listener)
            }
        }

        with(binding.commentBody) {
            setText(comment.body)
        }
    }
}

class ReplyViewHolder(private val binding: ItemReplyBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        with(binding.listReplies) {
            layoutManager = LinearLayoutManager(context)
            adapter = CommentAdapter(comment)
        }
    }
}

class MoreViewHolder(private val binding: ItemMoreBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(more: MoreEntity, isFirst: Boolean) {
        binding.more = more
        if (!isFirst) {
            val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins((more.depth + 1) * 15, 0, 0, 0) // TODO: Fix offset
            binding.root.layoutParams = params
        }
    }
}