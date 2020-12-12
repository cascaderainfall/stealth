package com.cosmos.unreddit.api

import com.cosmos.unreddit.api.pojo.details.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditApi {

    // TODO: Implement limit
    // TODO: Suspend

    //region Subreddit

    @GET("/r/{subreddit}/.json")
    suspend fun getSubreddit(@Path("subreddit") subreddit: String,
                             @Query("after") after: String? = null): Listing

    @GET("/r/{subreddit}/{sort}.json")
    fun getSortedSubreddit(@Path("subreddit") subreddit: String,
                           @Path("sort") sort: Sort,
                           @Query("t") topSorting: TopSorting?): Call<Listing>

    @GET("/r/{subreddit}/about.json")
    suspend fun getSubredditInfo(@Path("subreddit") subreddit: String): Child

    @GET("/r/{subreddit}/about/rules.json")
    fun getSubredditRules(@Path("subreddit") subreddit: String): Call<Child>

    //endregion

    @GET("{permalink}.json")
    suspend fun getPost(@Path("permalink", encoded = true) permalink: String,
                        @Query("limit") limit: Int? = null): List<Listing>

    //region User

    @GET("/user/{user}/about.json")
    suspend fun getUserInfo(@Path("user") user: String): Child

    @GET("/user/{user}/submitted/.json")
    suspend fun getUserPosts(@Path("user") user: String,
                             @Query("after") after: String? = null): Listing

    @GET("/user/{user}/comments/.json")
    suspend fun getUserComments(@Path("user") user: String,
                                @Query("after") after: String? = null): Listing

    //endregion

    enum class Sort(type: String) {
        HOT("hot"), NEW("new"), TOP("top"), RISING("rising")
    }

    enum class TopSorting(type: String) {
        HOUR("hour"), DAY("day"), WEEK("week"), MONTH("month"),
        YEAR("year"), ALL("all")
    }

    companion object {
        const val BASE_URL = "https://www.reddit.com/"
    }
}