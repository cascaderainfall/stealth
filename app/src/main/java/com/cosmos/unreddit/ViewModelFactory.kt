package com.cosmos.unreddit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cosmos.unreddit.postlist.PostListRepository
import com.cosmos.unreddit.postlist.PostListViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(PostListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return PostListViewModel(PostListRepository.getInstance(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}