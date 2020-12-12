package com.cosmos.unreddit.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.unreddit.databinding.FragmentSubscriptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionsFragment : Fragment() {

    private var _binding: FragmentSubscriptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubscriptionsViewModel by activityViewModels()

    private lateinit var adapter: SubscriptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubscriptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner, { subscriptions ->
            adapter.submitData(subscriptions)
        })
    }

    private fun initRecyclerView() {
        adapter = SubscriptionsAdapter()
        binding.listSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        binding.listSubscriptions.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SubscriptionsFragment"
    }
}