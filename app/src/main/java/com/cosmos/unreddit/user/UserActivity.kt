package com.cosmos.unreddit.user

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.size.Precision
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.cosmos.unreddit.ViewModelFactory
import com.cosmos.unreddit.databinding.ActivityUserBinding
import com.google.android.material.tabs.TabLayoutMediator

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    private val viewModel: UserViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()
        initUser()
        bindViewModel()
    }

    private fun initViewPager() {
        val userStateAdapter = UserStateAdapter(this)
        binding.viewPager.apply {
            adapter = userStateAdapter
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setText(UserStateAdapter.TABS[position].title)
        }.attach()
    }

    private fun initUser() {
        val data: Uri? = intent?.data
        data?.let {
            val user = getUserFromUri(it.toString())
            viewModel.setUser(user)
        }
    }

    private fun bindViewModel() {
        viewModel.about.observe(this, this::bindInfo)
    }

    private fun bindInfo(user: User) {
        with (user) {
            binding.user = this

            binding.userImage.load(icon) {
                crossfade(true)
                scale(Scale.FILL)
                precision(Precision.AUTOMATIC)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun getUserFromUri(uriString: String): String {
        // TODO: Find a better way to handle URIs?
        return uriString.substringAfterLast(USER_URI)
    }

    companion object {
        private const val USER_URI = "content://reddit/user/"
    }
}