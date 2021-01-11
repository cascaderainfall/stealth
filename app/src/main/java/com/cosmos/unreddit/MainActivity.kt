package com.cosmos.unreddit

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.cosmos.unreddit.databinding.ActivityMainBinding
import com.cosmos.unreddit.postlist.PostListFragment
import com.cosmos.unreddit.preferences.PreferencesFragment
import com.cosmos.unreddit.subscriptions.SubscriptionsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: UiViewModel by viewModels()

    // TODO: Move to Navigation Component
    private val postListFragment: PostListFragment by lazy { PostListFragment() }
    private val subscriptionsFragment: SubscriptionsFragment by lazy { SubscriptionsFragment() }
    private val preferencesFragment: PreferencesFragment by lazy { PreferencesFragment() }
    private lateinit var active: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        with(supportFragmentManager) {
            beginTransaction()
                .add(R.id.fragment_container, preferencesFragment, PreferencesFragment.TAG)
                .hide(preferencesFragment)
                .commit()
            beginTransaction()
                .add(R.id.fragment_container, subscriptionsFragment, SubscriptionsFragment.TAG)
                .hide(subscriptionsFragment)
                .commit()
            beginTransaction()
                .add(R.id.fragment_container, postListFragment, PostListFragment.TAG)
                .commit()
        }
        active = postListFragment

        viewModel.navigationVisibility.observe(this, this::showNavigation)
    }

    private fun initNavigation() {
        with(binding.bottomNavigation) {
            setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        supportFragmentManager.beginTransaction()
                            .hide(active)
                            .show(postListFragment)
                            .commit()
                        active = postListFragment
                        true
                    }
                    R.id.subscriptions -> {
                        supportFragmentManager.beginTransaction()
                            .hide(active)
                            .show(subscriptionsFragment)
                            .commit()
                        active = subscriptionsFragment
                        true
                    }
                    R.id.settings -> {
                        supportFragmentManager.beginTransaction()
                            .hide(active)
                            .show(preferencesFragment)
                            .commit()
                        active = preferencesFragment
                        true
                    }
                    else -> false
                }
            }
            setOnNavigationItemReselectedListener { item ->
                when (item.itemId) {
                    R.id.home -> postListFragment.scrollToTop()
                }
            }
        }
    }

    private fun showNavigation(show: Boolean) {
        val transition = Slide().apply {
            duration = 500
            addTarget(binding.bottomNavigation)
        }
        TransitionManager.beginDelayedTransition(binding.root, transition)
        binding.bottomNavigation.visibility = if (show) View.VISIBLE else View.GONE
    }
}
