package com.rwawrzyniak.securephotos

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.main_activity){
	private lateinit var appBarConfiguration: AppBarConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val navController = getNavController()
		appBarConfiguration = AppBarConfiguration(navController.graph)
		setupActionBarWithNavController(navController, appBarConfiguration)

	}

	override fun onResume() {
		super.onResume()
		val navController = getNavController()
		navigateToAppCodeScreenIfNotAlreadyDisplayed(navController)
	}

	override fun onSupportNavigateUp(): Boolean {
		return getNavController().navigateUp(appBarConfiguration)
				|| super.onSupportNavigateUp()
	}

	private fun getNavController(): NavController {
		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		val navController = navHostFragment.navController
		return navController
	}

	private fun navigateToAppCodeScreenIfNotAlreadyDisplayed(navController: NavController) {
		// TODO avoid hardcoded label for fragment
		// TODO Block ON Back if appCode screen displayed
		if (navController.currentDestination?.label ?: -1 != "AppCodeFragment")
			navController.navigate(R.id.action_global_appCodeFragment)
	}
}
