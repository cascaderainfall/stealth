package com.cosmos.unreddit.model

enum class PosterType(val value: Int) {
    REGULAR(0), ADMIN(1), MODERATOR(2);

    companion object {
        fun toType(type: Int): PosterType {
            return when (type) {
                REGULAR.value -> REGULAR
                ADMIN.value -> ADMIN
                MODERATOR.value -> MODERATOR
                else -> REGULAR
            }
        }

        fun fromDistinguished(distinguished: String?): PosterType {
            return when (distinguished) {
                "admin" -> ADMIN
                "moderator" -> MODERATOR
                else -> REGULAR
            }
        }
    }
}
