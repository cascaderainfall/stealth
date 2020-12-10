package com.cosmos.unreddit.user

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cosmos.unreddit.database.UserMapper
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.postlist.PostListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserViewModel(private val repository: PostListRepository) : ViewModel() {

    private val _user: MutableLiveData<String> = MutableLiveData()
    val user: LiveData<String>
        get() = _user

    val about: LiveData<User> = _user.switchMap { user ->
        repository.getUserInfo(user)
            .map { UserMapper.dataToEntity(it.data) }
            .asLiveData()
    }

    val history: Flow<List<String>> = repository.getHistory().map { list -> list.map { it.postId } }

    val showNsfw: Flow<Boolean> = repository.getShowNsfw()

    private var currentPosts: Flow<PagingData<PostEntity>>? = null

    fun loadPosts(user: String): Flow<PagingData<PostEntity>> {
        val lastPosts = currentPosts
        if (_user.value == user && lastPosts != null) {
            return lastPosts
        }

        val newPosts = repository.getUserPosts(user).cachedIn(viewModelScope)
        currentPosts = newPosts

        return newPosts
    }

    private var currentComments: Flow<PagingData<Comment>>? = null

    fun loadComments(user: String): Flow<PagingData<Comment>> {
        val lastComments = currentComments
        if (_user.value == user && lastComments != null) {
            return lastComments
        }

        val newComments = repository.getUserComments(user).cachedIn(viewModelScope)
        currentComments = newComments

        return newComments
    }

    fun setUser(user: String) {
        if (_user.value != user) {
            _user.value = user
        }
    }
}