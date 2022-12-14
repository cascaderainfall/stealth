package com.cosmos.unreddit.util.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cosmos.unreddit.UnredditApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.launchRepeat(state: Lifecycle.State, block: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state) {
            block()
        }
    }
}

val Fragment.unredditApplication: UnredditApplication?
    get() = activity?.unredditApplication
