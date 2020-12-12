package com.cosmos.unreddit.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

class PreferencesDataStore constructor(context: Context) {

    private val dataStore: DataStore<Preferences> = context.createDataStore(name = "preferences")

    fun setString(key: Preferences.Key<String>, value: String) {
        setValue(key, value)
    }

    fun getString(key: Preferences.Key<String>, defaultValue: String): Flow<String> {
        return getValue(key, defaultValue)
    }

    fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        setValue(key, value)
    }

    fun getBoolean(key: Preferences.Key<Boolean>, defaultValue: Boolean = false): Flow<Boolean> {
        return getValue(key, defaultValue)
    }

    fun setInt(key: Preferences.Key<Int>, value: Int) {
        setValue(key, value)
    }

    fun getInt(key: Preferences.Key<Int>, defaultValue: Int = -1): Flow<Int> {
        return getValue(key, defaultValue)
    }

    private fun <T> setValue(key: Preferences.Key<T>, value: T) {
        GlobalScope.launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    private fun <T> getValueSync(key: Preferences.Key<T>, defaultValue: T): T {
        return runBlocking { getValue(key, defaultValue).first() }
    }

    private fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
}