package com.cosmos.unreddit.postlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.unreddit.ViewModelFactory
import com.cosmos.unreddit.databinding.FragmentPostBinding
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.util.PostUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PostListFragment : Fragment(), PostListAdapter.PostClickListener {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostListViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var adapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner, { subscriptions ->
            when {
                subscriptions.isNotEmpty() -> loadPosts(subscriptions.joinToString("+"))
                else -> loadPosts(DEFAULT_SUBREDDIT)
            }
        })
    }

    private fun initRecyclerView() {
        adapter = PostListAdapter(PostListRepository.getInstance(requireContext()), this)
        binding.listPost.layoutManager = LinearLayoutManager(requireContext())
        binding.listPost.adapter = adapter
    }

    private fun loadPosts(subreddit: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            PostUtil.filterPosts(viewModel.loadPosts(subreddit), viewModel.history, viewModel.showNsfw).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    fun scrollToTop() {
        binding.listPost.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(post: PostEntity) {
        Toast.makeText(context, post.domain, Toast.LENGTH_SHORT).show() // TODO
    }

    override fun onLongClick(post: PostEntity) {
        Toast.makeText(context, post.domain, Toast.LENGTH_SHORT).show() // TODO
    }

    override fun onImageClick(post: PostEntity) {
        Toast.makeText(context, post.preview, Toast.LENGTH_SHORT).show() // TODO
    }

    override fun onVideoClick(post: PostEntity) {
        Toast.makeText(context, post.preview, Toast.LENGTH_SHORT).show() // TODO
    }

    override fun onLinkClick(post: PostEntity) {
        Toast.makeText(context, post.url, Toast.LENGTH_SHORT).show() // TODO
    }

    companion object {
        const val TAG = "PostListFragment"

        private const val DEFAULT_SUBREDDIT = "popular"

        @JvmStatic
        fun newInstance() = PostListFragment()
    }
}