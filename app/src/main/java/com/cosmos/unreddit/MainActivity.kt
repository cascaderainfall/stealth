package com.cosmos.unreddit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cosmos.unreddit.databinding.ActivityMainBinding
import com.cosmos.unreddit.postlist.PostListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val postListFragment: PostListFragment by lazy { PostListFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, postListFragment)
            .commit()
    }
}