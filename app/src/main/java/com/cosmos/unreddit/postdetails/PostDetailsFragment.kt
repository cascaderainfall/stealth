package com.cosmos.unreddit.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Precision
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.cosmos.unreddit.R
import com.cosmos.unreddit.databinding.FragmentPostDetailsBinding
import com.cosmos.unreddit.databinding.IncludePostInfoBinding
import com.cosmos.unreddit.databinding.IncludePostTitleBinding
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.post.PostType
import com.cosmos.unreddit.view.FullscreenBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : FullscreenBottomSheetFragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostDetailsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val post = arguments?.getParcelable(KEY_POST_ENTITY) as? PostEntity
        post?.let {
            viewModel.setPost(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailsBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindViewModel() {
        viewModel.cachedPost.observe(viewLifecycleOwner, { cachedPost ->
            when (cachedPost.type) {
                PostType.TEXT -> bindPostText(cachedPost)
                PostType.IMAGE, PostType.VIDEO -> bindPostImage(cachedPost)
                PostType.LINK -> bindPostLink(cachedPost)
            }
        })
        viewModel.post.observe(viewLifecycleOwner, { post ->
            // TODO: Necessary?
            when (post.type) {
                PostType.TEXT -> {
                    val layout = binding.layoutIncludeText
//                    bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)
                }
                PostType.IMAGE, PostType.VIDEO -> {
                    val layout = binding.layoutIncludeImage
//                    bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)
                }
                PostType.LINK -> {
                    val layout = binding.layoutIncludeLink
//                    bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)
                }
            }
        })
        viewModel.comments.observe(viewLifecycleOwner, { comments ->
            // TODO: Move declaration out of observer
            val adapters: List<CommentAdapter> = comments.map { comment ->
                CommentAdapter(comment)
            }
            val concatAdapterConfig = ConcatAdapter.Config.Builder()
                .setIsolateViewTypes(false)
                .build()
            val concatAdapter = ConcatAdapter(concatAdapterConfig, adapters)
            with(binding.listComments) {
                layoutManager = LinearLayoutManager(context)
                adapter = concatAdapter
            }
        })
    }

    private fun bindPost(post: PostEntity, rootView: View,
                         postTitleBinding: IncludePostTitleBinding,
                         postInfoBinding: IncludePostInfoBinding) {
        rootView.visibility = View.VISIBLE
        postTitleBinding.post = post
        postInfoBinding.post = post
    }

    private fun bindPostText(post: PostEntity) {
        val layout = binding.layoutIncludeText
//        bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)

        if (!post.selfText.isNullOrEmpty()) {
            layout.textPostSelf.text = post.selfText
            layout.textPostSelf.maxHeight = Int.MAX_VALUE
        } else {
            layout.textPostSelf.visibility = View.GONE
        }
    }

    private fun bindPostImage(post: PostEntity) {
        val layout = binding.layoutIncludeImage
//        bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)

        layout.imagePostPreview.load(post.preview) {
            crossfade(true)
            scale(Scale.FILL)
            precision(Precision.AUTOMATIC)
            transformations(RoundedCornersTransformation(0F, 0F, 25F, 25F))
        }
    }

    private fun bindPostLink(post: PostEntity) {
        val layout = binding.layoutIncludeLink
//        bindPost(post, layout.root, layout.includePostTitle, layout.includePostInfo)

        layout.imagePostLinkPreview.load(post.preview) {
            crossfade(true)
            scale(Scale.FILL)
            precision(Precision.AUTOMATIC)
            transformations(RoundedCornersTransformation(25F))
        }
    }

    override fun getTheme(): Int {
        return R.style.PostDetailsSheetTheme
    }

    companion object {
        const val TAG = "PostDetailsFragment"

        private const val KEY_POST_ENTITY = "KEY_POST_ENTITY"

        @JvmStatic
        fun newInstance(post: PostEntity) = PostDetailsFragment().apply {
            arguments = bundleOf(
                KEY_POST_ENTITY to post
            )
        }
    }
}