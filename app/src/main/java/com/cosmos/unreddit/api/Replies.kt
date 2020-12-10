package com.cosmos.unreddit.api

import com.cosmos.unreddit.api.pojo.details.Listing
import com.squareup.moshi.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class Replies

class RepliesAdapter {
    @FromJson
    @Replies
    fun fromJson(reader: JsonReader, defaultAdapter: JsonAdapter<Listing>): Listing? {
        return if (reader.peek() === JsonReader.Token.STRING) {
            reader.skipValue()
            null
        } else {
            defaultAdapter.fromJson(reader)
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, @Replies value: Listing,
               defaultAdapter: JsonAdapter<Listing>) {
        defaultAdapter.toJson(writer, value)
    }
}