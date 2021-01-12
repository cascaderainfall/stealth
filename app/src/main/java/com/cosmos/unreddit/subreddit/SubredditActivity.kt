package com.cosmos.unreddit.subreddit

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cosmos.unreddit.R
import com.cosmos.unreddit.databinding.ActivitySubredditBinding
import com.cosmos.unreddit.util.RedditUri
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubredditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubredditBinding

    private val viewModel: SubredditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubredditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSubreddit()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SubredditFragment.newInstance())
            .commit()
    }

    private fun initSubreddit() {
        val data: Uri? = intent?.data
        val type = RedditUri.getUriType(data)
        if (type == RedditUri.UriType.SUBREDDIT) {
            val subreddit = data?.lastPathSegment ?: return
            viewModel.setSubreddit(subreddit)
        }
    }
}
