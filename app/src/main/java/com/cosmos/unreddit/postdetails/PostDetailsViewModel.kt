package com.cosmos.unreddit.postdetails

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cosmos.unreddit.api.pojo.details.Listing
import com.cosmos.unreddit.api.pojo.details.PostChild
import com.cosmos.unreddit.database.CommentMapper
import com.cosmos.unreddit.database.PostMapper
import com.cosmos.unreddit.post.Comment
import com.cosmos.unreddit.post.PostEntity
import com.cosmos.unreddit.postlist.PostListRepository
import kotlinx.coroutines.launch

class PostDetailsViewModel
@ViewModelInject constructor(private val repository: PostListRepository) : ViewModel() {

    private val _cachedPost: MediatorLiveData<PostEntity> = MediatorLiveData()
    val cachedPost: LiveData<PostEntity>
        get() = _cachedPost

    private val _listings: LiveData<List<Listing>> = _cachedPost.switchMap {
        repository.getPost(it.permalink).asLiveData()
    }

    val post: LiveData<PostEntity> = _listings.switchMap {
        liveData { emit(PostMapper.dataToEntity((it[0].data.children[0] as PostChild).data)) }
    }

    val comments: LiveData<List<Comment>> = _listings.switchMap {
        liveData { emit(CommentMapper.dataToEntities(it[1].data.children)) }
    }

    fun setPost(post: PostEntity) {
        if (_cachedPost.value != post) {
            _cachedPost.value = post
        }
    }
}