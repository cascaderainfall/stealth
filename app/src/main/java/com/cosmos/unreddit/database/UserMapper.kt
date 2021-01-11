package com.cosmos.unreddit.database

import com.cosmos.unreddit.api.pojo.AboutUserData
import com.cosmos.unreddit.api.pojo.details.AboutUserChild
import com.cosmos.unreddit.api.pojo.details.Child
import com.cosmos.unreddit.api.pojo.details.ChildType
import com.cosmos.unreddit.user.User

object UserMapper {

    fun dataToEntity(data: AboutUserData): User {
        with(data) {
            return User(
                isSuspended,
                name,
                subreddit?.title,
                subreddit?.over18 ?: false,
                iconImg,
                subreddit?.url,
                subreddit?.publicDescription,
                linkKarma,
                commentKarma,
                getTimeInMillis()
            )
        }
    }

    fun dataToEntities(data: List<Child>?): List<User> {
        val userList = mutableListOf<User>()

        data?.forEach {
            if (it.kind == ChildType.t2) {
                with((it as AboutUserChild).data) {
                    if (!isSuspended) {
                        userList.add(dataToEntity(this))
                    }
                }
            }
        }

        return userList
    }
}
