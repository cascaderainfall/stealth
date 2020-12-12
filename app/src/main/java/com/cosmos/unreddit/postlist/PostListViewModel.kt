package com.cosmos.unreddit.postlist

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cosmos.unreddit.post.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostListViewModel
@ViewModelInject constructor(private val repository: PostListRepository) : ViewModel() {

    val subscriptions: LiveData<List<String>> = repository.getSubscriptions()
        .map { list -> list.map { it.name } }
        .asLiveData()

    val history: Flow<List<String>> = repository.getHistory().map { list -> list.map { it.postId } }

    val showNsfw: Flow<Boolean> = repository.getShowNsfw()

    private var currentSubreddit: String? = null

    private var currentPosts: Flow<PagingData<PostEntity>>? = null

    fun loadPosts(subreddit: String): Flow<PagingData<PostEntity>> {
        val lastPosts = currentPosts
        if (currentSubreddit == subreddit && lastPosts != null) {
            return lastPosts
        }

        currentSubreddit = subreddit

        val newPosts = repository.getPosts(subreddit).cachedIn(viewModelScope)
        currentPosts = newPosts

        return newPosts
    }
}