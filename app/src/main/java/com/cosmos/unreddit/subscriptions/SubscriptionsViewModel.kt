package com.cosmos.unreddit.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.cosmos.unreddit.postlist.PostListRepository
import com.cosmos.unreddit.subreddit.Subscription

class SubscriptionsViewModel(repository: PostListRepository) : ViewModel() {

    val subscriptions: LiveData<List<Subscription>> = repository.getSubscriptions().asLiveData()

}