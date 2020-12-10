package com.cosmos.unreddit.postlist

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.load
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.cosmos.unreddit.databinding.*
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.view.AwardGroup
import com.cosmos.unreddit.view.RedditTextView
import com.google.android.material.chip.Chip

abstract class PostViewHolder(itemView: View,
                              private val postTitleBinding: IncludePostTitleBinding,
                              private val postInfoBinding: IncludePostInfoBinding,
                              private val listener: PostListAdapter.PostClickListener)
    : RecyclerView.ViewHolder(itemView) {

    // TODO: flair

    open fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
        postTitleBinding.post = postEntity
        postInfoBinding.post = postEntity

        with(itemView) {
            setOnClickListener {
                onClick(position)
                listener.onClick(postEntity)
            }
            setOnLongClickListener {
                listener.onLongClick(postEntity)
                return@setOnLongClickListener true
            }
        }
    }

    class ImagePostViewHolder(binding: ItemPostImageBinding,
                              private val listener: PostListAdapter.PostClickListener)
        : PostViewHolder(binding.root, binding.includePostTitle, binding.includePostInfo, listener) {
        private val preview: ImageView = binding.imagePostPreview
        private val awards: AwardGroup = binding.awards

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            awards.setAwards(postEntity.awards)

            with(preview) {
                load(postEntity.preview) {
                    crossfade(true)
                    scale(Scale.FILL)
                    precision(Precision.AUTOMATIC)
                    transformations(RoundedCornersTransformation(0F, 0F, 25F, 25F))
                }
                setOnClickListener { listener.onImageClick(postEntity) }
            }
        }
    }

    class TextPostViewHolder(binding: ItemPostTextBinding,
                             private val listener: PostListAdapter.PostClickListener)
        : PostViewHolder(binding.root, binding.includePostTitle, binding.includePostInfo, listener) {
        private val selfText: RedditTextView = binding.textPostSelf
        private val awards: AwardGroup = binding.awards

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            awards.setAwards(postEntity.awards)

            with(selfText) {
                if (!postEntity.selfText.isNullOrEmpty()) {
                    setText(postEntity.selfText, false)
                } else {
                    visibility = View.GONE
                }
            }
        }
    }

    class LinkPostViewHolder(binding: ItemPostLinkBinding,
                             private val listener: PostListAdapter.PostClickListener)
        : PostViewHolder(binding.root, binding.includePostTitle,
        binding.includePostInfo, listener) {
        private val preview: ImageView = binding.imagePostLinkPreview
        private val awards: AwardGroup = binding.awards

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            awards.setAwards(postEntity.awards)

            with(preview) {
                load(postEntity.preview) {
                    crossfade(true)
                    scale(Scale.FILL)
                    precision(Precision.AUTOMATIC)
                    transformations(RoundedCornersTransformation(25F))
                }
                setOnClickListener { listener.onLinkClick(postEntity) }
            }
        }
    }

    class PollPostViewHolder() {

    }
}