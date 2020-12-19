package com.cosmos.unreddit.di

import com.cosmos.unreddit.api.EditedAdapter
import com.cosmos.unreddit.api.RawJsonInterceptor
import com.cosmos.unreddit.api.RedditApi
import com.cosmos.unreddit.api.RepliesAdapter
import com.cosmos.unreddit.api.SortingConverterFactory
import com.cosmos.unreddit.api.pojo.details.AboutChild
import com.cosmos.unreddit.api.pojo.details.AboutUserChild
import com.cosmos.unreddit.api.pojo.details.Child
import com.cosmos.unreddit.api.pojo.details.ChildType
import com.cosmos.unreddit.api.pojo.details.CommentChild
import com.cosmos.unreddit.api.pojo.details.MoreChild
import com.cosmos.unreddit.api.pojo.details.PostChild
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(PolymorphicJsonAdapterFactory.of(Child::class.java, "kind")
                .withSubtype(CommentChild::class.java, ChildType.t1.name)
                .withSubtype(AboutUserChild::class.java, ChildType.t2.name)
                .withSubtype(PostChild::class.java, ChildType.t3.name)
                .withSubtype(AboutChild::class.java, ChildType.t5.name)
                .withSubtype(MoreChild::class.java, ChildType.more.name))
            .add(RepliesAdapter())
            .add(EditedAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(RawJsonInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRedditApi(moshi: Moshi, okHttpClient: OkHttpClient): RedditApi {
        return Retrofit.Builder()
            .baseUrl(HttpUrl.parse(RedditApi.BASE_URL)!!)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(SortingConverterFactory())
            .client(okHttpClient)
            .build()
            .create(RedditApi::class.java)
    }
}
