package com.cosmos.unreddit.ui.base

import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.cosmos.unreddit.NavigationGraphDirections
import com.cosmos.unreddit.R
import com.cosmos.unreddit.data.model.GalleryMedia
import com.cosmos.unreddit.data.model.MediaType
import com.cosmos.unreddit.data.model.db.PostEntity
import com.cosmos.unreddit.ui.common.widget.RedditView
import com.cosmos.unreddit.ui.linkmenu.LinkMenuFragment
import com.cosmos.unreddit.ui.postdetails.PostDetailsFragment
import com.cosmos.unreddit.ui.postlist.PostListAdapter
import com.cosmos.unreddit.ui.postmenu.PostMenuFragment
import com.cosmos.unreddit.util.LinkUtil

open class BaseFragment : Fragment(), PostListAdapter.PostClickListener,
    RedditView.OnLinkClickListener {

    protected open val viewModel: BaseViewModel? = null

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val navOptions: NavOptions by lazy {
        NavOptions.Builder()
            .setEnterAnim(R.anim.nav_enter_anim)
            .setExitAnim(R.anim.nav_exit_anim)
            .setPopEnterAnim(R.anim.nav_enter_anim)
            .setPopExitAnim(R.anim.nav_exit_anim)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    protected open fun onBackPressed() {
        onBackPressedCallback.isEnabled = false
        findNavController().navigateUp()
    }

    protected fun navigate(directions: NavDirections, navOptions: NavOptions = this.navOptions) {
        findNavController().navigate(directions, navOptions)
    }

    protected fun navigate(deepLink: Uri, navOptions: NavOptions = this.navOptions) {
        findNavController().navigate(deepLink, navOptions)
    }

    override fun onClick(post: PostEntity) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.nav_enter_anim,
                R.anim.nav_exit_anim,
                R.anim.nav_enter_anim,
                R.anim.nav_exit_anim
            )
            .add(
                R.id.fragment_container,
                PostDetailsFragment.newInstance(post),
                PostDetailsFragment.TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onLongClick(post: PostEntity) {
        PostMenuFragment.show(parentFragmentManager, post)
    }

    override fun onMenuClick(post: PostEntity) {
        PostMenuFragment.show(parentFragmentManager, post)
    }

    override fun onImageClick(post: PostEntity) {
        if (post.gallery.isNotEmpty()) {
            openGallery(post.gallery)
        } else {
            openMedia(post.mediaUrl, post.mediaType)
        }
    }

    override fun onVideoClick(post: PostEntity) {
        openMedia(post.mediaUrl, post.mediaType)
    }

    override fun onLinkClick(post: PostEntity) {
        onLinkClick(post.url)
    }

    override fun onLinkClick(link: String) {
        onLinkClick(link, LinkUtil.getLinkType(link))
    }

    open fun onLinkClick(link: String, mediaType: MediaType) {
        when (mediaType) {
            MediaType.REDDIT_SUBREDDIT -> {
                val subreddit = link.removePrefix("/r/")
                openSubreddit(subreddit)
            }

            MediaType.REDDIT_USER -> {
                val user = link.removePrefix("/u/")
                openUser(user)
            }

            MediaType.REDDIT_LINK -> openRedditLink(link)

            MediaType.REDDIT_WIKI -> openBrowser(link)

            MediaType.IMGUR_ALBUM,
            MediaType.IMGUR_GALLERY,
            MediaType.IMGUR_GIF,
            MediaType.IMGUR_VIDEO,
            MediaType.IMGUR_IMAGE,
            MediaType.REDGIFS,
            MediaType.STREAMABLE,
            MediaType.IMAGE,
            MediaType.VIDEO -> openMedia(link, mediaType)

            else -> openBrowser(link)
        }
    }

    override fun onLinkLongClick(link: String) {
        LinkMenuFragment.show(parentFragmentManager, link)
    }

    override fun onSaveClick(post: PostEntity) {
        viewModel?.toggleSavePost(post)
    }

    open fun openGallery(images: List<GalleryMedia>) {
        navigate(NavigationGraphDirections.openGallery(images.toTypedArray()))
    }

    open fun openMedia(link: String, mediaType: MediaType) {
        navigate(NavigationGraphDirections.openMedia(link, mediaType))
    }

    open fun openSubreddit(subreddit: String) {
        navigate(NavigationGraphDirections.openSubreddit(subreddit))
    }

    open fun openUser(user: String) {
        navigate(NavigationGraphDirections.openUser(user))
    }

    open fun openRedditLink(link: String) {
        navigate(Uri.parse(link))
    }

    open fun openBrowser(link: String) {
        navigate(NavigationGraphDirections.openBrowser(link))
    }
}
