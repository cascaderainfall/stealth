package com.cosmos.unreddit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cosmos.unreddit.databinding.ActivityMainBinding
import com.cosmos.unreddit.postlist.PostListFragment
import com.cosmos.unreddit.subscriptions.SubscriptionsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val postListFragment: PostListFragment by lazy { PostListFragment() }
    private val subscriptionsFragment: SubscriptionsFragment by lazy { SubscriptionsFragment() }
    private lateinit var active: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()

        with (supportFragmentManager) {
            beginTransaction()
                .add(R.id.fragment_container, subscriptionsFragment, SubscriptionsFragment.TAG)
                .hide(subscriptionsFragment)
                .commit()
            beginTransaction()
                .add(R.id.fragment_container, postListFragment, PostListFragment.TAG)
                .commit()
        }
        active = postListFragment
    }

    private fun initNavigation() {
        with (binding.bottomNavigation) {
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
}