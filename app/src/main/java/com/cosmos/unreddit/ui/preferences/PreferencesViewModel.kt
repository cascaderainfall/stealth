package com.cosmos.unreddit.ui.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cosmos.unreddit.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val nightMode: LiveData<Int> = preferencesRepository.getNightMode().asLiveData()

    val showNsfw: LiveData<Boolean> = preferencesRepository.getShowNsfw()
        .asLiveData()

    val showNsfwPreview: LiveData<Boolean> = preferencesRepository.getShowNsfwPreview()
        .asLiveData()

    val showSpoilerPreview: LiveData<Boolean> = preferencesRepository.getShowSpoilerPreview()
        .asLiveData()

    fun setNightMode(nightMode: Int) {
        viewModelScope.launch {
            preferencesRepository.setNightMode(nightMode)
        }
    }

    fun setShowNsfw(showNsfw: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowNsfw(showNsfw)
        }
    }

    fun setShowNsfwPreview(showNsfwPreview: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowNsfwPreview(showNsfwPreview)
        }
    }

    fun setShowSpoilerPreview(showSpoilerPreview: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowSpoilerPreview(showSpoilerPreview)
        }
    }
}