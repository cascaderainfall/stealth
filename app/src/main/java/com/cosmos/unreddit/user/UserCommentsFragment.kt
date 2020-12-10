package com.cosmos.unreddit.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.unreddit.ViewModelFactory
import com.cosmos.unreddit.databinding.FragmentUserBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserCommentsFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels { ViewModelFactory(requireContext()) }

    private lateinit var adapter: UserCommentsAdapter

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
        adapter = UserCommentsAdapter()
        binding.listContent.layoutManager = LinearLayoutManager(requireContext())
        binding.listContent.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.user.observe(viewLifecycleOwner, { user ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadComments(user).collectLatest {
                    adapter.submitData(it)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}