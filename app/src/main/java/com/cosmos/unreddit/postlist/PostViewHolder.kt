package com.cosmos.unreddit.postlist

import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Precision
import coil.size.Scale
import com.cosmos.unreddit.databinding.*
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.view.RedditTextView
import com.google.android.material.card.MaterialCardView

abstract class PostViewHolder(itemView: View,
                              private val postInfoBinding: IncludePostInfoBinding,
                              private val postMetricsBinding: IncludePostMetricsBinding,
                              private val title: TextView,
                              private val listener: PostListAdapter.PostClickListener)
    : RecyclerView.ViewHolder(itemView) {

    // TODO: flair

    open fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
        postInfoBinding.post = postEntity
        postMetricsBinding.post = postEntity

        with(title) {
            text = postEntity.title
            setTextColor(postEntity.getSeenColor(title.context))
        }

        postMetricsBinding.awards.setAwards(postEntity.awards)

        with(postInfoBinding.textPostAuthor) {
            val width = paint.measureText(postEntity.author)
            val gradientShader = LinearGradient(0F, 0F, width, textSize,
                postEntity.getAuthorGradientColors(context), null, Shader.TileMode.CLAMP)
            paint.shader = gradientShader
        }

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
        : PostViewHolder(binding.root, binding.includePostInfo, binding.includePostMetrics, binding.textPostTitle, listener) {
        private val preview: ImageView = binding.imagePostPreview

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            with(preview) {
                load(postEntity.preview) {
                    crossfade(true)
                    scale(Scale.FILL)
                    precision(Precision.AUTOMATIC)
                }
                setOnClickListener { listener.onImageClick(postEntity) }
            }
        }
    }

    class TextPostViewHolder(binding: ItemPostTextBinding,
                             private val listener: PostListAdapter.PostClickListener)
        : PostViewHolder(binding.root, binding.includePostInfo, binding.includePostMetrics, binding.textPostTitle, listener) {
        private val selfText: RedditTextView = binding.textPostSelf
        private val selfTextCard: MaterialCardView = binding.textPostSelfCard

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            with(selfText) {
                if (!postEntity.selfText.isNullOrEmpty()) {
                    setText(postEntity.selfText, false)
                    setTextColor(postEntity.getSeenColor(context))
                } else {
                    selfTextCard.visibility = View.GONE
                }
            }
        }
    }

    class LinkPostViewHolder(binding: ItemPostLinkBinding,
                             private val listener: PostListAdapter.PostClickListener)
        : PostViewHolder(binding.root, binding.includePostInfo, binding.includePostMetrics, binding.textPostTitle, listener) {
        private val preview: ImageView = binding.imagePostLinkPreview

        override fun bind(postEntity: PostEntity, position: Int, onClick: (Int) -> Unit) {
            super.bind(postEntity, position, onClick)

            with(preview) {
                load(postEntity.preview) {
                    crossfade(true)
                    scale(Scale.FILL)
                    precision(Precision.AUTOMATIC)
                }
                setOnClickListener { listener.onLinkClick(postEntity) }
            }
        }
    }

    class PollPostViewHolder() {

    }
}