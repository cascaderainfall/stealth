package com.cosmos.unreddit.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Flair : Parcelable {
    @IgnoredOnParcel
    private val _data = mutableListOf<Pair<String, FlairType>>()
    val data: List<Pair<String, FlairType>> get() = _data

    fun addData(resource: String, type: FlairType) {
        _data.add(Pair(resource, type))
    }

    fun isEmpty(): Boolean = _data.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Flair) {
            return false
        }
        return this._data == other._data
    }

    override fun hashCode(): Int {
        return _data.hashCode()
    }

    enum class FlairType {
        TEXT, IMAGE
    }
}
