package com.cosmos.unreddit.di

import android.content.Context
import com.cosmos.unreddit.preferences.PreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): PreferencesDataStore {
        return PreferencesDataStore(context)
    }
}