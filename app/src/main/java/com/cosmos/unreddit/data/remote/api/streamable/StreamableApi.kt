package com.cosmos.unreddit.data.remote.api.streamable

import com.cosmos.unreddit.data.remote.api.streamable.model.Video
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamableApi {

    @GET("/videos/{shortcode}")
    suspend fun getVideo(@Path("shortcode") shortcode: String): Video

    companion object {
        const val BASE_URL = "https://api.streamable.com/"
    }
}
