package com.rwawrzyniak.securephotos.core.android

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BasicFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId), OnBackPressedListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun ignoreBackPress(): Boolean = false
}
