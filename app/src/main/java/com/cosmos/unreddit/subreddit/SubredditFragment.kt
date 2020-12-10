package com.cosmos.unreddit.subreddit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Precision
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.cosmos.unreddit.ViewModelFactory
import com.cosmos.unreddit.databinding.FragmentSubredditBinding
import com.cosmos.unreddit.databinding.LayoutSubredditAboutBinding
import com.cosmos.unreddit.databinding.LayoutSubredditContentBinding
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.postlist.PostListAdapter
import com.cosmos.unreddit.postlist.PostListRepository
import com.cosmos.unreddit.util.PostUtil
import com.cosmos.unreddit.util.SwipeListener
import com.cosmos.unreddit.util.setStatusBarColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class SubredditFragment : Fragment(), PostListAdapter.PostClickListener, SwipeListener.Callback,
    View.OnClickListener {

    private var _binding: FragmentSubredditBinding? = null
    private val binding get() = _binding!!

    private var _bindingContent: LayoutSubredditContentBinding? = null
    private val bindingContent get() = _bindingContent!!

    private var _bindingAbout: LayoutSubredditAboutBinding? = null
    private val bindingAbout get() = _bindingAbout!!

    private val viewModel: SubredditViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var adapter: PostListAdapter

    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val subreddit = arguments?.getString(KEY_SUBREDDIT)
        subreddit?.let {
            viewModel.setSubreddit(it)
        }
//        gestureDetector = GestureDetectorCompat(context, SwipeListener(this)) TODO
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditBinding.inflate(inflater, container, false)
        _bindingContent = binding.subredditContent
        _bindingAbout = binding.subredditAbout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
//        initDrawerGestures() TODO
        bindViewModel()

        bindingAbout.subredditSubscribeButton.setOnClickListener(this)
    }

    private fun bindViewModel() {
        viewModel.about.observe(viewLifecycleOwner, this::bindInfo)
        viewModel.isSubscribed.observe(viewLifecycleOwner, { isSubscribed ->
            with (bindingAbout.subredditSubscribeButton) {
                text = if (isSubscribed) {
                    "Unsubscribe" // TODO
                } else {
                    "Subscribe" // TODO
                }
            }
        })
        viewModel.subreddit.observe(viewLifecycleOwner, { subreddit ->
            viewLifecycleOwner.lifecycleScope.launch {
                PostUtil.filterPosts(viewModel.loadPosts(subreddit), viewModel.history, viewModel.showNsfw).collectLatest {
                    adapter.submitData(it)
                }
            }
        })
    }

    private fun initRecyclerView() {
        adapter = PostListAdapter(PostListRepository.getInstance(requireContext()), this)
        bindingContent.listPost.layoutManager = LinearLayoutManager(requireContext())
        bindingContent.listPost.adapter = adapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initDrawerGestures() {
        bindingContent.layoutRoot.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
        bindingContent.listPost.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    private fun bindInfo(about: SubredditEntity) {
        with(about) {
            bindingContent.subreddit = this
            bindingAbout.subreddit = this

            activity?.setStatusBarColor(primaryColor)
            bindingContent.subredditHeader.setBackgroundColor(primaryColor)

            // TODO: Manage no icon
            bindingContent.subredditImage.load(icon) {
                crossfade(true)
                scale(Scale.FILL)
                precision(Precision.AUTOMATIC)
                transformations(CircleCropTransformation())
            }
            bindingContent.subredditHeader.load(header) {
                crossfade(true)
                scale(Scale.FILL)
                precision(Precision.AUTOMATIC)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bindingContent = null
        _bindingAbout = null
    }

    override fun onClick(post: PostEntity) {
        //TODO("Not yet implemented")
    }

    override fun onLongClick(post: PostEntity) {
        //TODO("Not yet implemented")
    }

    override fun onImageClick(post: PostEntity) {
        //TODO("Not yet implemented")
    }

    override fun onVideoClick(post: PostEntity) {
        //TODO("Not yet implemented")
    }

    override fun onLinkClick(post: PostEntity) {
        //TODO("Not yet implemented")
    }

    override fun onSwipeLeft() {
        binding.root.openDrawer(GravityCompat.END)
    }

    override fun onSwipeRight() {
        // Ignore
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            bindingAbout.subredditSubscribeButton.id -> {
                if (viewModel.getIsSubscribedValue()) {
                    viewModel.unsubscribe()
                } else {
                    viewModel.subscribe()
                }
            }
        }
    }

    companion object {
        private const val KEY_SUBREDDIT = "KEY_SUBREDDIT"

        @JvmStatic
        fun newInstance(subreddit: String) = SubredditFragment().apply {
            arguments = bundleOf(
                KEY_SUBREDDIT to subreddit
            )
        }

        fun newInstance() = SubredditFragment()
    }
}