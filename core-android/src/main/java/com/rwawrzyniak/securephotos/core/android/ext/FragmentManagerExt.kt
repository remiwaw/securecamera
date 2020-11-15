package com.rwawrzyniak.securephotos.core.android.ext

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.transaction(commitNow: Boolean = true, body: FragmentTransaction.() -> Unit) {
    val transaction = beginTransaction()
    body(transaction)
    if (commitNow) transaction.commitNow() else transaction.commit()
}
