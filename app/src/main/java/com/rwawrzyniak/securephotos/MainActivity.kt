package com.rwawrzyniak.securephotos

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity){
	override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
		super.onCreate(savedInstanceState, persistentState)
	}

	override fun onResume() {
		super.onResume()
		val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		val navController = navHostFragment.navController
		navigateToAppCodeScreenIfNotAlreadyDisplayed(navController)
	}

	private fun navigateToAppCodeScreenIfNotAlreadyDisplayed(navController: NavController) {
		// TODO avoid hardcoded label for fragment
		// TODO Block ON Back if appCode screen displayed
		if (navController.currentDestination?.label ?: -1 != "AppCodeFragment")
			navController.navigate(R.id.action_global_appCodeFragment)
	}
}
