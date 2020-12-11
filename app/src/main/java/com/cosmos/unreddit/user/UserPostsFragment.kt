package com.cosmos.unreddit.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.unreddit.ViewModelFactory
import com.cosmos.unreddit.databinding.FragmentUserBinding
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.postlist.PostListAdapter
import com.cosmos.unreddit.postlist.PostListRepository
import com.cosmos.unreddit.util.PostUtil
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UserPostsFragment : Fragment(), PostListAdapter.PostClickListener {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var adapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        bindViewModel()
    }

    private fun initRecyclerView() {
        adapter = PostListAdapter(PostListRepository.getInstance(requireContext()), this)
        binding.listContent.layoutManager = LinearLayoutManager(requireContext())
        binding.listContent.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.user.observe(viewLifecycleOwner, { user ->
            viewLifecycleOwner.lifecycleScope.launch {
                PostUtil.filterPosts(viewModel.loadPosts(user), viewModel.history, viewModel.showNsfw).collectLatest {
                    adapter.submitData(it)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onImageClick(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onVideoClick(post: PostEntity) {
        TODO("Not yet implemented")
    }

    override fun onLinkClick(post: PostEntity) {
        TODO("Not yet implemented")
    }
}