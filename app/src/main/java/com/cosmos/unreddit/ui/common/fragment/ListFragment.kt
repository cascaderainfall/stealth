package com.cosmos.unreddit.ui.common.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.model.db.PostEntity
import com.cosmos.unreddit.databinding.ItemListContentBinding
import com.cosmos.unreddit.ui.base.BaseFragment
import com.cosmos.unreddit.ui.common.PostDividerItemDecoration
import com.cosmos.unreddit.ui.common.widget.PullToRefreshLayout
import com.cosmos.unreddit.ui.common.widget.PullToRefreshView
import com.cosmos.unreddit.util.DateUtil
import com.cosmos.unreddit.util.extension.applyWindowInsets
import com.cosmos.unreddit.util.extension.currentNavigationFragment

abstract class ListFragment<T : Adapter<out ViewHolder>> : BaseFragment(),
    PullToRefreshLayout.OnRefreshListener {

    private var _binding: ItemListContentBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var adapter: T

    protected open val showItemDecoration: Boolean = false

    protected open val enablePullToRefresh: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemListContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun applyInsets(view: View) {
        // Ignore
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        binding.pullRefresh.enablePullToRefresh = enablePullToRefresh
    }

    override fun onClick(post: PostEntity) {
        activity?.currentNavigationFragment?.let { currentFragment ->
            onClick(currentFragment.parentFragmentManager, post)
        }
    }

    protected open fun initRecyclerView() {
        adapter = createAdapter().apply {
            stateRestorationPolicy = PREVENT_WHEN_EMPTY
        }

        binding.listContent.apply {
            applyWindowInsets(left = false, top = false, right = false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListFragment.adapter
            if (showItemDecoration) {
                addItemDecoration(PostDividerItemDecoration(context))
            }
        }

        binding.pullRefresh.setOnRefreshListener(this)
    }

    protected fun setRefreshTime(timeInMillis: Long) {
        val time = getString(R.string.last_refresh, DateUtil.getLocalizedTime(timeInMillis))
        (binding.pullRefresh.refreshView as? PullToRefreshView)?.setLastRefresh(time)
    }

    protected abstract fun createAdapter(): T

    protected fun showRetryBar() {
        if (!binding.loadingState.infoRetry.isVisible) {
            binding.loadingState.infoRetry.show()
        }
    }

    override fun onRefresh() {
        // Not implemented
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
