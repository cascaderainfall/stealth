package com.cosmos.unreddit.subreddit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cosmos.unreddit.database.SubredditMapper
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SubredditViewModel
@ViewModelInject constructor(private val repository: PostListRepository) : ViewModel() {

    private val _subreddit: MutableLiveData<String> = MutableLiveData()
    val subreddit: LiveData<String>
        get() = _subreddit

    val about: LiveData<SubredditEntity> = _subreddit.switchMap { subreddit ->
        repository.getSubredditInfo(subreddit)
            .map { SubredditMapper.dataToEntity(it.data) }
            .asLiveData()
    }

    val isSubscribed: LiveData<Boolean> = _subreddit.switchMap { subreddit ->
        repository.getSubscriptions()
            .map { list -> list.any { it.name.equals(subreddit, ignoreCase = true) } }
            .asLiveData()
    }

    val history: Flow<List<String>> = repository.getHistory().map { list -> list.map { it.postId } }

    val showNsfw: Flow<Boolean> = repository.getShowNsfw()

    private var currentPosts: Flow<PagingData<PostEntity>>? = null

    fun loadPosts(subreddit: String): Flow<PagingData<PostEntity>> {
        val lastPosts = currentPosts
        if (_subreddit.value == subreddit && lastPosts != null) {
            return lastPosts
        }

        val newPosts = repository.getPosts(subreddit).cachedIn(viewModelScope)
        currentPosts = newPosts

        return newPosts
    }

    fun getIsSubscribedValue(): Boolean {
        return isSubscribed.value!!
    }

    fun setSubreddit(subreddit: String) {
        if (_subreddit.value != subreddit) {
            _subreddit.value = subreddit
        }
    }

    fun subscribe() {
        viewModelScope.launch {
            repository.subscribe(getSubredditName(), about.value?.icon)
        }
    }

    fun unsubscribe() {
        viewModelScope.launch {
            repository.unsubscribe(getSubredditName())
        }
    }

    private fun getSubredditName(): String {
        return about.value?.displayName ?: subreddit.value!!
    }
}