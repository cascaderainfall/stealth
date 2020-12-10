package com.cosmos.unreddit.api

import com.squareup.moshi.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class Edited

class EditedAdapter {
    @FromJson
    @Edited
    fun fromJson(reader: JsonReader, defaultAdapter: JsonAdapter<Long>): Long {
        return if (reader.peek() === JsonReader.Token.BOOLEAN) {
            reader.skipValue()
            -1L
        } else {
            defaultAdapter.fromJson(reader)!!
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, @Edited value: Long,
               defaultAdapter: JsonAdapter<Long>) {
        defaultAdapter.toJson(writer, value)
    }
}