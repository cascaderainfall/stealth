package com.cosmos.unreddit.user

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cosmos.unreddit.R
import com.cosmos.unreddit.databinding.ActivityUserBinding
import com.cosmos.unreddit.util.RedditUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUser()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, UserFragment())
            .commit()
    }

    private fun initUser() {
        val data: Uri? = intent?.data
        val type = RedditUri.getUriType(data)
        if (type == RedditUri.UriType.USER) {
            val user = data?.lastPathSegment ?: return
            viewModel.setUser(user)
        }
    }
}
