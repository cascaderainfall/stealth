package com.cosmos.unreddit.postlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cosmos.unreddit.databinding.ItemPostImageBinding
import com.cosmos.unreddit.databinding.ItemPostLinkBinding
import com.cosmos.unreddit.databinding.ItemPostTextBinding
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.post.PostType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostListAdapter(private val repository: PostListRepository,
                      private val listener: PostClickListener)
    : PagingDataAdapter<PostEntity, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    interface PostClickListener {
        fun onClick(post: PostEntity)

        fun onLongClick(post: PostEntity)

        fun onImageClick(post: PostEntity)

        fun onVideoClick(post: PostEntity)

        fun onLinkClick(post: PostEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            // Text post
            PostType.TEXT.value ->
                PostViewHolder.TextPostViewHolder(ItemPostTextBinding.inflate(inflater, parent, false), listener)
            // Image post
            PostType.IMAGE.value, PostType.VIDEO.value ->
                PostViewHolder.ImagePostViewHolder(ItemPostImageBinding.inflate(inflater, parent, false), listener)
            // Link post
            PostType.LINK.value ->
                PostViewHolder.LinkPostViewHolder(ItemPostLinkBinding.inflate(inflater, parent, false), listener)
            else -> throw IllegalArgumentException("Unknown type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type?.value ?: -1
    }

    override fun getItemCount(): Int {
        // TODO: Add loading item when network is loading item
        return super.getItemCount()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        when (getItemViewType(position)) {
            // Text post
            PostType.TEXT.value ->
                (holder as PostViewHolder.TextPostViewHolder).bind(item, position, this::onClick)
            // Image post
            PostType.IMAGE.value, PostType.VIDEO.value ->
                (holder as PostViewHolder.ImagePostViewHolder).bind(item, position, this::onClick)
            // Link post
            PostType.LINK.value ->
                (holder as PostViewHolder.LinkPostViewHolder).bind(item, position, this::onClick)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    private fun onClick(position: Int) {
        getItem(position)?.let {
            it.seen = true
            insertPostInHistory(it.id)
            notifyItemChanged(position, it) // TODO: Check with history in viewmodels
        }
    }

    private fun insertPostInHistory(id: String) {
        GlobalScope.launch {
            repository.insertPostInHistory(id)
        }
    }

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<PostEntity>() {
            override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
                return oldItem.score == newItem.score
                        && oldItem.commentsNumber == newItem.commentsNumber
                        && oldItem.totalAwards == newItem.totalAwards
                        && oldItem.isOC == newItem.isOC
                        && oldItem.flair == newItem.flair
                        && oldItem.selfText == newItem.selfText
                        && oldItem.isPinned == newItem.isPinned
                        && oldItem.isOver18 == newItem.isOver18
                        && oldItem.isSpoiler == newItem.isSpoiler
                        && oldItem.seen == newItem.seen
            }

        }
    }
}