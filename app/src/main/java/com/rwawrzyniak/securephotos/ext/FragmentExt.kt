@file:Suppress("UNCHECKED_CAST")

package com.rwawrzyniak.securephotos.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

fun <T : Fragment> Fragment.getOrAddFragment(
    containerViewId: Int = 0,
    tag: String? = null,
    commitNow: Boolean = true,
    init: () -> T
): T = childFragmentManager.getOrAddFragment(containerViewId, tag, commitNow, init)

private fun <T : Fragment> FragmentManager.getOrAddFragment(
    containerViewId: Int,
    tag: String?,
    commitNow: Boolean,
    init: () -> T
): T =
    findFragment(containerViewId, tag) ?: run {
        init().also {
            addFragment(containerViewId, tag, commitNow, it)
        }
    }

private fun <T : Fragment> FragmentManager.findFragment(containerViewId: Int, tag: String?): T? {
    require(containerViewId != 0 || tag != null) { "either containerViewId or tag must be given" }
    val byTag = tag?.let { findFragmentByTag(tag) } as T?
    if (byTag == null && containerViewId != 0) {
        return findFragmentById(containerViewId) as T?
    }
    return byTag
}


private fun FragmentManager.addFragment(
    containerViewId: Int = 0,
    tag: String?,
    commitNow: Boolean,
    fragment: Fragment
) {
    transaction(commitNow) {
        add(containerViewId, fragment, tag)
    }
}
