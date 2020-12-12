package com.cosmos.unreddit.subreddit

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cosmos.unreddit.R
import com.cosmos.unreddit.databinding.ActivitySubredditBinding
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
            .add(R.id.fragment_container, SubredditFragment.newInstance())
            .commit()
    }

    private fun initSubreddit() {
        val data: Uri? = intent?.data
        data?.let {
            val subreddit = getSubredditFromUri(it.toString())
            viewModel.setSubreddit(subreddit)
        }
    }

    private fun getSubredditFromUri(uriString: String): String {
        // TODO: Find a better way to handle URIs?
        return uriString.substringAfterLast(SUBREDDIT_URI)
    }

    companion object {
        private const val SUBREDDIT_URI = "content://reddit/subreddit/"
    }
}