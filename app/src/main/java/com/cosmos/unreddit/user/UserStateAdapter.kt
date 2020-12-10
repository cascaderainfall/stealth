package com.cosmos.unreddit.user

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cosmos.unreddit.R

class UserStateAdapter(fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = TABS.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserCommentsFragment()
            1 -> UserPostsFragment()
            else -> TODO()
        }
    }

    companion object {
        val TABS = arrayOf(UserTab.COMMENTS, UserTab.SUBMITTED)
    }

    enum class UserTab(@StringRes val title: Int) {
        COMMENTS(R.string.tab_user_comments),
        SUBMITTED(R.string.tab_user_submitted),
        @Deprecated("Deprecated in Redesign")
        GILDED(R.string.tab_user_gilded)
    }
}