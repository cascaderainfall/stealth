package com.cosmos.unreddit.ui.postdetails

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.local.mapper.CommentMapper
import com.cosmos.unreddit.data.model.Comment
import com.cosmos.unreddit.data.model.CommentEntity
import com.cosmos.unreddit.data.model.MoreEntity
import com.cosmos.unreddit.data.repository.PostListRepository
import com.cosmos.unreddit.databinding.ItemCommentBinding
import com.cosmos.unreddit.databinding.ItemMoreBinding
import com.cosmos.unreddit.ui.common.widget.RedditView
import com.cosmos.unreddit.util.extension.blurText
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CommentAdapter(
    context: Context,
    private val repository: PostListRepository,
    private val viewLifecycleOwner: LifecycleOwner,
    private val onLinkClickListener: RedditView.OnLinkClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val visibleComments = mutableListOf<Comment>()
    var linkId: String? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }

    private val commentOffset by lazy {
        context.resources.getDimension(R.dimen.comment_offset)
    }
    private val popInAnimation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.pop_in)
    }
    private val popOutAnimation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.pop_out)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Type.COMMENT.value ->
                CommentViewHolder(ItemCommentBinding.inflate(inflater, parent, false))
            Type.MORE.value ->
                MoreViewHolder(ItemMoreBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unknown type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (visibleComments[position]) {
            is CommentEntity -> Type.COMMENT.value
            is MoreEntity -> Type.MORE.value
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun getItemCount(): Int {
        return visibleComments.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            Type.COMMENT.value ->
                (holder as CommentViewHolder).bind(visibleComments[position] as CommentEntity)
            Type.MORE.value -> {
                (holder as MoreViewHolder).bind(visibleComments[position] as MoreEntity)
            }
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val comment = visibleComments[position]
            if (holder is CommentViewHolder && comment is CommentEntity) {
                holder.bindCommentHiddenIndicator(comment, true)
            }
        }
    }

    private fun onCommentClick(position: Int) {
        when (val comment = visibleComments[position]) {
            is CommentEntity -> {
                if (!comment.hasReplies) return

                val startIndex = position + 1

                if (!comment.isExpanded) {
                    comment.isExpanded = true

                    val replies = getExpandedReplies(comment.replies)
                    visibleComments.addAll(startIndex, replies)

                    notifyItemRangeInserted(startIndex, replies.size)
                } else {
                    comment.isExpanded = false

                    val replyCount = getReplyCount(startIndex, comment.depth)
                    comment.visibleReplyCount = replyCount

                    val endIndex = startIndex + replyCount
                    visibleComments.subList(startIndex, endIndex).clear()

                    notifyItemRangeRemoved(startIndex, replyCount)
                }

                notifyItemChanged(position, comment)
            }
            is MoreEntity -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    val link = linkId ?: return@launch

                    val containsMoreComments = comment.more.size > LOAD_MORE_LIMIT

                    val children = comment.more.take(LOAD_MORE_LIMIT).joinToString(",")
                    if (containsMoreComments) {
                        // Remove first 100 comments from list
                        with(comment) {
                            more.subList(0, LOAD_MORE_LIMIT).clear()
                            count = if (count > LOAD_MORE_LIMIT) count - LOAD_MORE_LIMIT else 0
                        }
                    }

                    repository.getMoreChildren(children, link).onStart {
                        comment.apply {
                            isLoading = true
                            isError = false
                        }
                        notifyItemChanged(position)
                    }.catch {
                        comment.apply {
                            isLoading = false
                            isError = true
                        }
                        notifyItemChanged(position)
                    }.map {
                        CommentMapper.dataToEntities(it.json.data.things)
                    }.map {
                        it.filter { comment -> comment.depth < COMMENT_DEPTH_LIMIT }
                    }.map {
                        restoreCommentHierarchy(it, comment.depth)
                    }.collectLatest { comments ->
                        comment.apply {
                            isLoading = false
                            isError = false
                        }

                        if (comment.depth > 0) {
                            val parentComment = visibleComments.find { it.name == comment.parent }

                            if (parentComment != null &&
                                parentComment is CommentEntity &&
                                parentComment.isExpanded
                            ) {
                                parentComment.replies.removeLastOrNull()
                                parentComment.replies.addAll(comments)
                                if (containsMoreComments) {
                                    parentComment.replies.add(comment)
                                }
                            } else {
                                return@collectLatest
                            }
                        }

                        visibleComments.removeAt(position)
                        notifyItemRemoved(position)
                        visibleComments.addAll(position, comments)

                        var itemCount = comments.size
                        if (containsMoreComments) {
                            visibleComments.add(position + itemCount, comment)
                            itemCount++
                        }

                        notifyItemRangeInserted(position, itemCount)
                    }
                }
            }
        }
    }

    private fun getExpandedReplies(comments: List<Comment>): List<Comment> {
        val replies = mutableListOf<Comment>()

        for (comment in comments) {
            when (comment) {
                is CommentEntity -> {
                    replies.add(comment)
                    if (comment.isExpanded) {
                        replies.addAll(getExpandedReplies(comment.replies))
                    }
                }
                is MoreEntity -> replies.add(comment)
            }
        }

        return replies
    }

    private fun getReplyCount(index: Int, depth: Int): Int {
        var count = 0

        for (i in index until visibleComments.size) {
            if (visibleComments[i].depth > depth) {
                count++
            } else {
                break
            }
        }

        return count
    }

    private fun restoreCommentHierarchy(comments: List<Comment>, depth: Int): List<Comment> {
        val restored = mutableListOf<Comment>()

        for (i in comments.indices) {
            val comment = comments[i]

            if (comment.depth > depth) {
                continue
            } else if (comment.depth < depth) {
                break
            }

            val nextComment = comments.getOrNull(i + 1)

            if (comment is CommentEntity && nextComment != null && nextComment.depth > depth) {
                comment.replies.addAll(
                    restoreCommentHierarchy(comments.subList(i + 1, comments.lastIndex), depth + 1)
                )
                comment.visibleReplyCount = comment.replies.size
            }

            restored.add(comment)
        }

        return restored
    }

    fun submitData(comments: List<Comment>) {
        val result = DiffUtil.calculateDiff(CommentDiffCallback(this.visibleComments, comments))

        this.visibleComments.clear()
        this.visibleComments.addAll(comments)

        result.dispatchUpdatesTo(this)
    }

    private inner class CommentViewHolder(
        private val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentEntity) {
            binding.comment = comment

            binding.commentAuthor.apply {
                setTextColor(ContextCompat.getColor(context, comment.posterType.color))
            }

            binding.commentScore.blurText(comment.scoreHidden)

            binding.commentColorIndicator.apply {
                if (comment.depth == 0) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    comment.commentIndicator?.let {
                        backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context, it)
                        )
                    }
                    val params = ConstraintLayout.LayoutParams(
                        layoutParams as ConstraintLayout.LayoutParams
                    ).apply {
                        marginStart = (commentOffset * (comment.depth - 1)).toInt()
                    }
                    layoutParams = params
                }
            }

            bindCommentHiddenIndicator(comment, false)

            binding.commentFlair.apply {
                if (!comment.flair.isEmpty()) {
                    visibility = View.VISIBLE

                    setFlair(comment.flair)
                } else {
                    visibility = View.GONE
                }
            }

            binding.commentAwards.apply {
                if (comment.awards.isNotEmpty()) {
                    visibility = View.VISIBLE

                    setAwards(comment.awards, comment.totalAwards)
                } else {
                    visibility = View.GONE
                }
            }

            binding.commentOpText.visibility = if (comment.isSubmitter) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onCommentClick(bindingAdapterPosition)
            }

            binding.commentBody.apply {
                setText(comment.body)
                setOnLinkClickListener(onLinkClickListener)
                setOnClickListener {
                    onCommentClick(bindingAdapterPosition)
                }
            }
        }

        fun bindCommentHiddenIndicator(comment: CommentEntity, showAnimation: Boolean) {
            binding.commentHiddenIndicator.apply {
                if (comment.hasReplies && !comment.isExpanded) {
                    visibility = View.VISIBLE
                    text = comment.visibleReplyCount.toString()
                    if (showAnimation) startAnimation(popInAnimation)
                } else {
                    visibility = View.GONE
                    if (showAnimation) startAnimation(popOutAnimation)
                }
            }
        }
    }

    private inner class MoreViewHolder(
        private val binding: ItemMoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(more: MoreEntity) {
            binding.more = more

            binding.progress.isVisible = more.isLoading
            binding.textError.isVisible = more.isError

            binding.commentBody.apply {
                val params = ConstraintLayout.LayoutParams(
                    layoutParams as ConstraintLayout.LayoutParams
                ).apply {
                    marginStart = if (more.depth > 0) {
                        (commentOffset * more.depth).toInt()
                    } else {
                        0
                    }
                }
                layoutParams = params
            }

            itemView.setOnClickListener {
                onCommentClick(bindingAdapterPosition)
            }
        }
    }

    private inner class CommentDiffCallback(
        private val oldComments: List<Comment>,
        private val newComments: List<Comment>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldComments.size
        }

        override fun getNewListSize(): Int {
            return newComments.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldComment = oldComments[oldItemPosition]
            val newComment = newComments[newItemPosition]

            return if (oldComment is CommentEntity && newComment is CommentEntity) {
                oldComment.name == newComment.name
            } else if (oldComment is MoreEntity && newComment is MoreEntity) {
                oldComment.id == newComment.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldComments[oldItemPosition] == newComments[newItemPosition]
        }
    }

    private enum class Type(val value: Int) {
        COMMENT(0), MORE(1)
    }

    companion object {
        private const val LOAD_MORE_LIMIT = 100
        private const val COMMENT_DEPTH_LIMIT = 10
    }
}