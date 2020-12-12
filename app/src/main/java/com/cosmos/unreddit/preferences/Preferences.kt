package com.cosmos.unreddit.preferences

import androidx.datastore.preferences.core.preferencesKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Preferences @Inject constructor(private val dataStore: PreferencesDataStore) {

    fun setShowNsfw(showNsfw: Boolean) {
        dataStore.setBoolean(PreferencesKeys.SHOW_NSFW, showNsfw)
    }

    fun getShowNsfw(defaultValue: Boolean = false): Flow<Boolean> {
        return dataStore.getBoolean(PreferencesKeys.SHOW_NSFW, defaultValue)
    }

    fun setShowNsfwPreview(showNsfwPreview: Boolean) {
        dataStore.setBoolean(PreferencesKeys.SHOW_NSFW_PREVIEW, showNsfwPreview)
    }

    fun getShowNsfwPreview(defaultValue: Boolean = false): Flow<Boolean> {
        return dataStore.getBoolean(PreferencesKeys.SHOW_NSFW_PREVIEW, defaultValue)
    }

    fun setShowSpoilerPreview(showSpoilerPreview: Boolean) {
        dataStore.setBoolean(PreferencesKeys.SHOW_SPOILER_PREVIEW, showSpoilerPreview)
    }

    fun getShowSpoilerPreview(defaultValue: Boolean = false): Flow<Boolean> {
        return dataStore.getBoolean(PreferencesKeys.SHOW_SPOILER_PREVIEW, defaultValue)
    }

    object PreferencesKeys {
        val SHOW_NSFW = preferencesKey<Boolean>("show_nsfw")
        val SHOW_NSFW_PREVIEW = preferencesKey<Boolean>("show_nsfw_preview")
        val SHOW_SPOILER_PREVIEW = preferencesKey<Boolean>("show_spoiler_preview")
    }
}