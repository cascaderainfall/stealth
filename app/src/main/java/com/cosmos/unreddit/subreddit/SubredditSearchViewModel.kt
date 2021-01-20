package com.cosmos.unreddit.subreddit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.post.Sorting
import com.cosmos.unreddit.postlist.PostListRepository
import com.cosmos.unreddit.util.PagerHelper
import com.cosmos.unreddit.util.PostUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SubredditSearchViewModel @ViewModelInject constructor(
    private val repository: PostListRepository
) : ViewModel() {

    private val history: Flow<List<String>> = repository.getHistoryIds()
        .distinctUntilChanged()

    private val showNsfw: Flow<Boolean> = repository.getShowNsfw()
        .distinctUntilChanged()

    private val _sorting: MutableStateFlow<Sorting> = MutableStateFlow(DEFAULT_SORTING)
    val sorting: StateFlow<Sorting> = _sorting

    private val _query: MutableStateFlow<String?> = MutableStateFlow(null)
    val query: StateFlow<String?> = _query

    private val _subreddit: MutableStateFlow<String?> = MutableStateFlow(null)
    val subreddit: StateFlow<String?> = _subreddit

    private val searchPagerHelper = object : PagerHelper<PostEntity>() {
        override fun getResults(query: String, sorting: Sorting): Flow<PagingData<PostEntity>> {
            return repository.searchInSubreddit(
                query,
                _subreddit.value ?: FALLBACK_SUBREDDIT,
                sorting
            ).cachedIn(viewModelScope)
        }
    }

    fun searchAndFilterPosts(query: String, sorting: Sorting): Flow<PagingData<PostEntity>> {
        return PostUtil.filterPosts(searchPagerHelper.loadData(query, sorting), history, showNsfw)
            .cachedIn(viewModelScope)
    }

    fun setQuery(subreddit: String) {
        if (_query.value != subreddit) {
            _query.value = subreddit
        }
    }

    fun setSorting(sorting: Sorting) {
        if (_sorting.value != sorting) {
            _sorting.value = sorting
        }
    }

    fun setSubreddit(subreddit: String) {
        if (_subreddit.value != subreddit) {
            _subreddit.value = subreddit
        }
    }

    companion object {
        private const val FALLBACK_SUBREDDIT = "all"

        private val DEFAULT_SORTING = Sorting(RedditApi.Sort.RELEVANCE, RedditApi.TimeSorting.ALL)
    }
}
