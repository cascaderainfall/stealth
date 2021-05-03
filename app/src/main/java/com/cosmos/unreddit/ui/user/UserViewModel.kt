package com.cosmos.unreddit.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.cosmos.unreddit.data.local.mapper.UserMapper
import com.cosmos.unreddit.data.model.Comment
import com.cosmos.unreddit.data.model.Resource
import com.cosmos.unreddit.data.model.Sorting
import com.cosmos.unreddit.data.model.User
import com.cosmos.unreddit.data.model.db.PostEntity
import com.cosmos.unreddit.data.model.preferences.ContentPreferences
import com.cosmos.unreddit.data.remote.api.reddit.RedditApi
import com.cosmos.unreddit.data.repository.PostListRepository
import com.cosmos.unreddit.data.repository.PreferencesRepository
import com.cosmos.unreddit.ui.base.BaseViewModel
import com.cosmos.unreddit.util.PagerHelper
import com.cosmos.unreddit.util.PostUtil
import com.cosmos.unreddit.util.extension.updateValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: PostListRepository,
    preferencesRepository: PreferencesRepository
) : BaseViewModel(preferencesRepository, repository) {

    val contentPreferences: Flow<ContentPreferences> =
        preferencesRepository.getContentPreferences()

    private val _sorting: MutableStateFlow<Sorting> = MutableStateFlow(DEFAULT_SORTING)
    val sorting: StateFlow<Sorting> = _sorting

    private val _user: MutableStateFlow<String?> = MutableStateFlow(null)
    val user: StateFlow<String?> = _user

    private val _page: MutableStateFlow<Int> = MutableStateFlow(0)
    val page: StateFlow<Int> get() = _page

    private val _about: MutableLiveData<Resource<User>> = MutableLiveData()
    val about: LiveData<Resource<User>> = _about

    private val savedCommentIds: Flow<List<String>> = currentProfile.flatMapConcat {
        repository.getSavedCommentIds(it.id).distinctUntilChanged()
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    private val postPagerHelper = object : PagerHelper<PostEntity>() {
        override fun getResults(query: String, sorting: Sorting): Flow<PagingData<PostEntity>> {
            return repository.getUserPosts(query, sorting).cachedIn(viewModelScope)
        }
    }

    private val commentPagerHelper = object : PagerHelper<Comment>() {
        override fun getResults(query: String, sorting: Sorting): Flow<PagingData<Comment>> {
            return repository.getUserComments(query, sorting).cachedIn(viewModelScope)
        }
    }

    fun loadAndFilterPosts(user: String, sorting: Sorting): Flow<PagingData<PostEntity>> {
        return PostUtil.filterPosts(
            postPagerHelper.loadData(user, sorting),
            historyIds,
            savedPostIds,
            contentPreferences
        ).cachedIn(viewModelScope)
    }

    fun loadAndFilterComments(user: String, sorting: Sorting): Flow<PagingData<Comment>> {
        return combine(
            commentPagerHelper.loadData(user, sorting),
            savedCommentIds
        ) { _comments, _savedIds ->
            _comments.map { comment ->
                comment.apply {
                    (this as? Comment.CommentEntity)?.saved = _savedIds.contains(this.name)
                }
            }
        }
    }

    fun loadUserInfo(forceUpdate: Boolean) {
        if (_user.value != null) {
            if (_about.value == null || forceUpdate) {
                loadUserInfo(_user.value!!)
            }
        } else {
            _about.value = Resource.Error()
        }
    }

    private fun loadUserInfo(user: String) {
        viewModelScope.launch {
            repository.getUserInfo(user).onStart {
                _about.value = Resource.Loading()
            }.catch {
                when (it) {
                    is IOException -> _about.value = Resource.Error(message = it.message)
                    is HttpException -> _about.value = Resource.Error(it.code(), it.message())
                    else -> _about.value = Resource.Error()
                }
            }.map {
                UserMapper.dataToEntity(it.data)
            }.collect {
                _about.value = Resource.Success(it)
            }
        }
    }

    fun setSorting(sorting: Sorting) {
        _sorting.updateValue(sorting)
    }

    fun setUser(user: String) {
        _user.updateValue(user)
    }

    fun setPage(position: Int) {
        _page.updateValue(position)
    }

    companion object {
        private val DEFAULT_SORTING = Sorting(RedditApi.Sort.NEW)
    }
}
