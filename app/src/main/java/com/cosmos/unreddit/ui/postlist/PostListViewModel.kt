package com.cosmos.unreddit.ui.postlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cosmos.unreddit.data.local.mapper.PostMapper2
import com.cosmos.unreddit.data.model.Data
import com.cosmos.unreddit.data.model.Sort
import com.cosmos.unreddit.data.model.Sorting
import com.cosmos.unreddit.data.model.db.PostEntity
import com.cosmos.unreddit.data.model.db.Profile
import com.cosmos.unreddit.data.model.preferences.ContentPreferences
import com.cosmos.unreddit.data.repository.PostListRepository
import com.cosmos.unreddit.data.repository.PreferencesRepository
import com.cosmos.unreddit.di.DispatchersModule.DefaultDispatcher
import com.cosmos.unreddit.ui.base.BaseViewModel
import com.cosmos.unreddit.util.PostUtil
import com.cosmos.unreddit.util.extension.updateValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel
@Inject constructor(
    private val repository: PostListRepository,
    private val preferencesRepository: PreferencesRepository,
    private val postMapper: PostMapper2,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel(preferencesRepository, repository) {

    val contentPreferences: Flow<ContentPreferences> =
        preferencesRepository.getContentPreferences()

    val profiles: Flow<List<Profile>> = repository.getAllProfiles()

    private val _sorting: MutableStateFlow<Sorting> = MutableStateFlow(DEFAULT_SORTING)
    val sorting: StateFlow<Sorting> = _sorting

    val subreddit: Flow<List<String>> = subscriptionsNames
        .distinctUntilChanged()
        .map { subscriptions ->
            if (subscriptions.isNotEmpty()) {
                subscriptions.shuffled()
            } else {
                listOf(DEFAULT_SUBREDDIT)
            }
        }
        .flowOn(defaultDispatcher)

    val postDataFlow: Flow<PagingData<PostEntity>>

    val fetchData: StateFlow<Data.FetchMultiple> = combine(
        subreddit,
        sorting
    ) { subreddit, sorting ->
        Data.FetchMultiple(subreddit, sorting)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Data.FetchMultiple(listOf(DEFAULT_SUBREDDIT), DEFAULT_SORTING)
    )

    private var latestUser: Data.User? = null

    private val userData: Flow<Data.User> = combine(
        historyIds, savedPostIds, contentPreferences
    ) { history, saved, prefs ->
        Data.User(history, saved, prefs)
    }.onEach {
        latestUser = it
    }.distinctUntilChangedBy {
        it.contentPreferences
    }

    private val _lastRefresh: MutableStateFlow<Long> = MutableStateFlow(System.currentTimeMillis())
    val lastRefresh: StateFlow<Long> = _lastRefresh.asStateFlow()

    var isDrawerOpen: Boolean = false

    init {
        postDataFlow = fetchData
            // Fetch last user data when search data is updated and merge them together
            .flatMapLatest { fetchData -> userData.map { fetchData to it } }
            .flatMapLatest { getPosts(it.first, it.second) }
            .onEach { _lastRefresh.value = System.currentTimeMillis() }
            .cachedIn(viewModelScope)
    }

    private fun getPosts(data: Data.FetchMultiple, user: Data.User): Flow<PagingData<PostEntity>> {
        return repository.getPosts(data.query, data.sorting)
            .map { pagingData ->
                PostUtil.filterPosts(pagingData, latestUser ?: user, postMapper, defaultDispatcher)
            }
    }

    fun setSorting(sorting: Sorting) {
        _sorting.updateValue(sorting)
    }

    fun selectProfile(profile: Profile) {
        viewModelScope.launch {
            preferencesRepository.setCurrentProfile(profile.id)
        }
    }

    companion object {
        private const val DEFAULT_SUBREDDIT = "popular"
        private val DEFAULT_SORTING = Sorting(Sort.HOT)
    }
}
