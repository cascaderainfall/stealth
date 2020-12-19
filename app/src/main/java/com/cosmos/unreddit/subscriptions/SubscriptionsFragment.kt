package com.cosmos.unreddit.subscriptions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.cosmos.unreddit.databinding.FragmentSubscriptionsBinding
import com.cosmos.unreddit.util.RedditUri
import com.cosmos.unreddit.util.showSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionsFragment : Fragment() {

    private var _binding: FragmentSubscriptionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubscriptionsViewModel by activityViewModels()

    private lateinit var adapter: SubscriptionsAdapter

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppBar()
        initRecyclerView()
        bindViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (binding.appBar.searchInput.isVisible) {
                    showSearchInput(false)
                }
            }
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(this, onBackPressedCallback)
    }

    private fun bindViewModel() {
        viewModel.subscriptions.observe(viewLifecycleOwner, { subscriptions ->
            adapter.submitData(subscriptions)
        })
    }

    private fun initRecyclerView() {
        adapter = SubscriptionsAdapter { onClick(it) }
        binding.listSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        binding.listSubscriptions.adapter = adapter
    }

    private fun initAppBar() {
        with(binding.appBar) {
            searchCard.setOnClickListener {
                showSearchInput(true)
            }
            searchInput.apply {
                doOnTextChanged { text, _, _, _ ->
                    viewModel.setSearchQuery(text.toString())
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_SEARCH -> {
                            // TODO: Launch search request
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun showSearchInput(show: Boolean) {
        onBackPressedCallback.isEnabled = show

        with(binding.appBar) {
            val transition = Slide().apply {
                duration = 125
                addTarget(label)
                addTarget(searchCard)
                addTarget(searchInput)
                addListener(object : Transition.TransitionListener {
                    override fun onTransitionStart(transition: Transition) {
                        // ignore
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        if (!show) {
                            searchInput.text?.clear()
                        }
                    }

                    override fun onTransitionCancel(transition: Transition) {
                        // ignore
                    }

                    override fun onTransitionPause(transition: Transition) {
                        // ignore
                    }

                    override fun onTransitionResume(transition: Transition) {
                        // ignore
                    }
                })
            }
            TransitionManager.beginDelayedTransition(root, transition)
            label.visibility = if (show) View.GONE else View.VISIBLE
            searchCard.visibility = if (show) View.GONE else View.VISIBLE
            searchInput.visibility = if (show) View.VISIBLE else View.GONE

            if (show) {
                searchInput.showSoftKeyboard()
            }
        }
    }

    private fun onClick(subreddit: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = RedditUri.getSubredditUri(subreddit)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SubscriptionsFragment"
    }
}
