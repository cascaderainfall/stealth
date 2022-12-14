package com.cosmos.unreddit.ui.subreddit

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.model.Resource
import com.cosmos.unreddit.data.model.db.PostEntity
import com.cosmos.unreddit.data.model.db.SubredditEntity
import com.cosmos.unreddit.data.repository.PostListRepository
import com.cosmos.unreddit.databinding.FragmentSubredditBinding
import com.cosmos.unreddit.databinding.LayoutSubredditAboutBinding
import com.cosmos.unreddit.databinding.LayoutSubredditContentBinding
import com.cosmos.unreddit.ui.base.BaseFragment
import com.cosmos.unreddit.ui.common.widget.PullToRefreshLayout
import com.cosmos.unreddit.ui.common.widget.PullToRefreshView
import com.cosmos.unreddit.ui.loadstate.NetworkLoadStateAdapter
import com.cosmos.unreddit.ui.postlist.PostListAdapter
import com.cosmos.unreddit.ui.postmenu.PostMenuFragment
import com.cosmos.unreddit.ui.sort.SortFragment
import com.cosmos.unreddit.util.DateUtil
import com.cosmos.unreddit.util.extension.addLoadStateListener
import com.cosmos.unreddit.util.extension.applyWindowInsets
import com.cosmos.unreddit.util.extension.betterSmoothScrollToPosition
import com.cosmos.unreddit.util.extension.clearSortingListener
import com.cosmos.unreddit.util.extension.clearWindowInsetsListener
import com.cosmos.unreddit.util.extension.launchRepeat
import com.cosmos.unreddit.util.extension.loadSubredditIcon
import com.cosmos.unreddit.util.extension.onRefreshFromNetwork
import com.cosmos.unreddit.util.extension.setSortingListener
import com.cosmos.unreddit.util.extension.toPixels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SubredditFragment : BaseFragment(), PopupMenu.OnMenuItemClickListener,
    PullToRefreshLayout.OnRefreshListener {

    private var _binding: FragmentSubredditBinding? = null
    private val binding get() = _binding!!

    private var _bindingContent: LayoutSubredditContentBinding? = null
    private val bindingContent get() = _bindingContent!!

    private var _bindingAbout: LayoutSubredditAboutBinding? = null
    private val bindingAbout get() = _bindingAbout!!

    override val viewModel: SubredditViewModel by viewModels()

    private val args: SubredditFragmentArgs by navArgs()

    private lateinit var postListAdapter: PostListAdapter

    private var isSubscribeEnabled: Boolean
        get() = bindingAbout.subredditSubscribeButton.isEnabled
        set(value) {
            bindingAbout.subredditSubscribeButton.isEnabled = value
        }

    @Inject
    lateinit var repository: PostListRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setSubreddit(args.subreddit.removeSuffix("/"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubredditBinding.inflate(inflater, container, false)
        _bindingContent = binding.subredditContent
        _bindingAbout = binding.subredditAbout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingContent.root.applyWindowInsets(left = false, right = false, bottom = false)

        initResultListener()
        initAppBar()
        initRecyclerView()
        initDrawer()
        bindViewModel()
        bindingAbout.subredditSubscribeButton.setOnClickListener { toggleSubscription() }
        bindingContent.loadingState.infoRetry.setActionClickListener { retry() }

        viewModel.contentLayoutProgress?.let { bindingContent.layoutRoot.progress = it }
        viewModel.drawerContentLayoutProgress?.let { binding.drawerContent.progress = it }
    }

    override fun applyInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { rootView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.subredditAbout.subredditName.run {
                updatePadding(
                    paddingLeft,
                    insets.top,
                    paddingRight,
                    paddingBottom
                )
            }

            rootView.clearWindowInsetsListener()

            windowInsets
        }
    }

    private fun bindViewModel() {
        launchRepeat(Lifecycle.State.STARTED) {
            launch {
                viewModel.contentPreferences.collect {
                    postListAdapter.contentPreferences = it
                }
            }

            launch {
                viewModel.searchData.collect {
                    bindingContent.loadingState.infoRetry.hide()
                }
            }

            launch {
                viewModel.subreddit.collect { subreddit ->
                    subreddit.takeIf { it.isNotBlank() }?.let {
                        viewModel.loadSubredditInfo(false)
                    }
                }
            }

            launch {
                viewModel.sorting.collect {
                    bindingContent.sortIcon.setSorting(it)
                }
            }

            launch {
                viewModel.postDataFlow.collectLatest {
                    postListAdapter.submitData(it)
                }
            }

            launch {
                viewModel.about.collect {
                    when (it) {
                        is Resource.Success -> bindInfo(it.data)
                        is Resource.Error -> handleError(it.code)
                        is Resource.Loading -> {
                            // ignore
                        }
                    }
                }
            }

            launch {
                viewModel.isSubscribed.collect { isSubscribed ->
                    with(bindingAbout.subredditSubscribeButton) {
                        visibility = View.VISIBLE
                        text = if (isSubscribed) {
                            getString(R.string.subreddit_button_unsubscribe)
                        } else {
                            getString(R.string.subreddit_button_subscribe)
                        }
                    }
                }
            }

            launch {
                viewModel.isDescriptionCollapsed.collect { isCollapsed ->
                    // TODO: Animate layout changes
                    val maxHeight = if (isCollapsed) {
                        requireContext().toPixels(DESCRIPTION_MAX_HEIGHT).toInt()
                    } else {
                        Integer.MAX_VALUE
                    }
                    ConstraintSet().apply {
                        clone(bindingAbout.layoutRoot)
                        constrainMaxHeight(R.id.subreddit_public_description, maxHeight)
                        applyTo(bindingAbout.layoutRoot)
                    }
                }
            }

            launch {
                viewModel.lastRefresh.collect {
                    val time = getString(R.string.last_refresh, DateUtil.getLocalizedTime(it))
                    (binding.subredditContent.pullRefresh.refreshView as? PullToRefreshView)
                        ?.setLastRefresh(time)
                }
            }
        }
    }

    private fun initRecyclerView() {
        postListAdapter = PostListAdapter(repository, this, this).apply {
            addLoadStateListener(
                bindingContent.listPost,
                bindingContent.loadingState,
                bindingContent.pullRefresh
            ) {
                showRetryBar()
            }
        }
        bindingContent.listPost.apply {
            applyWindowInsets(left = false, top = false, right = false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postListAdapter.withLoadStateHeaderAndFooter(
                header = NetworkLoadStateAdapter { postListAdapter.retry() },
                footer = NetworkLoadStateAdapter { postListAdapter.retry() }
            )
        }

        bindingContent.pullRefresh.setOnRefreshListener(this)

        launchRepeat(Lifecycle.State.STARTED) {
            postListAdapter.onRefreshFromNetwork {
                scrollToTop()
            }
        }
    }

    private fun initDrawer() {
        with(binding.drawerLayout) {
            setScrimColor(Color.TRANSPARENT)
            drawerElevation = 0F
        }
        bindingAbout.subredditPublicDescription.setOnClickListener {
            viewModel.toggleDescriptionCollapsed()
        }
    }

    private fun initAppBar() {
        with(bindingContent) {
            sortCard.setOnClickListener { showSortDialog() }
            backCard.setOnClickListener { onBackPressed() }
            moreCard.setOnClickListener { showMenu() }
            subredditName.setOnClickListener { scrollToTop() }
            subredditImage.setOnClickListener { scrollToTop() }
        }
    }

    private fun initResultListener() {
        setSortingListener { sorting -> sorting?.let { viewModel.setSorting(it) } }
    }

    private fun bindInfo(about: SubredditEntity) {
        viewModel.isSubredditReachable = true
        with(about) {
            bindingContent.subreddit = this
            bindingAbout.subreddit = this

            bindingContent.subredditImage.loadSubredditIcon(icon)

            if (publicDescription.isNotEmpty()) {
                bindingAbout.subredditPublicDescription.apply {
                    setText(publicDescription)
                    setOnLinkClickListener(this@SubredditFragment)
                }
            } else {
                bindingAbout.subredditPublicDescription.visibility = View.GONE
            }

            if (description.isNotEmpty()) {
                bindingAbout.subredditDescription.apply {
                    setText(description)
                    setOnLinkClickListener(this@SubredditFragment)
                }
            }

            isSubscribeEnabled = true
        }
    }

    private fun handleError(code: Int?) {
        isSubscribeEnabled = true
        when (code) {
            403 -> {
                viewModel.isSubredditReachable = false
                showUnauthorizedDialog()
            }
            404 -> {
                viewModel.isSubredditReachable = false
                showNotFoundDialog()
            }
            else -> showRetryBar()
        }
    }

    private fun retry() {
        if (viewModel.about.value is Resource.Error) {
            viewModel.loadSubredditInfo(true)
        }

        postListAdapter.retry() // TODO: Don't retry if not necessary
    }

    private fun showRetryBar() {
        if (!bindingContent.loadingState.infoRetry.isVisible) {
            bindingContent.loadingState.infoRetry.show()
        }
    }

    private fun scrollToTop() {
        bindingContent.listPost.betterSmoothScrollToPosition(0)
    }

    private fun showSearchFragment() {
        navigate(
            SubredditFragmentDirections.openSearch(
                viewModel.subreddit.value,
                viewModel.about.value.dataValue?.icon
            )
        )
    }

    private fun showSortDialog() {
        SortFragment.show(childFragmentManager, viewModel.sorting.value)
    }

    private fun showNotFoundDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_subreddit_not_found_title)
            .setMessage(R.string.dialog_subreddit_not_found_body)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ -> dialog.handleUserAcknowledgement() }
            .setCancelable(false)
            .show()
    }

    private fun showUnauthorizedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_subreddit_unauthorized_title)
            .setMessage(R.string.dialog_subreddit_unauthorized_body)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ -> dialog.handleUserAcknowledgement() }
            .setCancelable(false)
            .show()
    }

    private fun toggleSubscription() {
        if (!viewModel.isSubredditReachable) {
            isSubscribeEnabled = false
        }
        viewModel.toggleSubscription()
    }

    private fun showMenu() {
        PopupMenu(requireContext(), binding.subredditContent.moreCard)
            .apply {
                menuInflater.inflate(R.menu.subreddit_menu, this.menu)
                setOnMenuItemClickListener(this@SubredditFragment)
            }
            .show()
    }

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun DialogInterface.handleUserAcknowledgement() {
        if (viewModel.isSubscribed.value) {
            // Allow the user to unsubscribe
            dismiss()
        } else {
            onBackPressed()
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.search -> showSearchFragment()
            R.id.sidebar -> openDrawer()
            else -> {
                return false
            }
        }
        return true
    }

    override fun onRefresh() {
        postListAdapter.refresh()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (binding.subredditContent.pullRefresh.refreshView as? PullToRefreshLayout.RefreshCallback)
            ?.reset()

        // Save progress of MotionLayout to restore it in case of fragment recreation
        // currentState is not always properly updated
        viewModel.contentLayoutProgress = bindingContent.layoutRoot.progress
        viewModel.drawerContentLayoutProgress = binding.drawerContent.progress

        clearSortingListener()

        _binding = null
        _bindingContent = null
        _bindingAbout = null
    }

    override fun onLongClick(post: PostEntity) {
        PostMenuFragment.show(parentFragmentManager, post, PostMenuFragment.MenuType.SUBREDDIT)
    }

    override fun onMenuClick(post: PostEntity) {
        PostMenuFragment.show(parentFragmentManager, post, PostMenuFragment.MenuType.SUBREDDIT)
    }

    companion object {
        private const val DESCRIPTION_MAX_HEIGHT = 200F
    }
}
