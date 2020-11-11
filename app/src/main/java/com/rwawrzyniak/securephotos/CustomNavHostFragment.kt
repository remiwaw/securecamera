package com.rwawrzyniak.securephotos

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomNavHostFragment: NavHostFragment(){

	@Inject
	lateinit var fragmentFactory: CustomFragmentFactory

	override fun onAttach(context: Context) {
		super.onAttach(context)
		childFragmentManager.fragmentFactory = fragmentFactory
	}

}
