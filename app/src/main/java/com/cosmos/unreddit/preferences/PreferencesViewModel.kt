package com.cosmos.unreddit.preferences

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class PreferencesViewModel
@ViewModelInject constructor(private val preferences: Preferences) : ViewModel() {

    val showNsfw: LiveData<Boolean> = preferences.getShowNsfw().asLiveData()

    val showNsfwPreview: LiveData<Boolean> = preferences.getShowNsfwPreview().asLiveData()

    val showSpoilerPreview: LiveData<Boolean> = preferences.getShowSpoilerPreview().asLiveData()

    fun setShowNsfw(showNsfw: Boolean) {
        preferences.setShowNsfw(showNsfw)
    }

    fun setShowNsfwPreview(showNsfwPreview: Boolean) {
        preferences.setShowNsfwPreview(showNsfwPreview)
    }

    fun setShowSpoilerPreview(showSpoilerPreview: Boolean) {
        preferences.setShowSpoilerPreview(showSpoilerPreview)
    }
}